package com.hubis.acs.middleware.work;

import com.hubis.acs.common.cache.BaseConstantCache;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.NodeMaster;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.position.cache.RobotPositionCache;
import com.hubis.acs.common.position.model.GlobalZone;
import com.hubis.acs.common.position.model.Position;
import com.hubis.acs.common.utils.CommonUtils;
import com.hubis.acs.common.utils.ConvertUtils;
import com.hubis.acs.common.utils.RuntimeUtils;
import com.hubis.acs.ui.work.CreateTransferControl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component("middleware_move_complete")
public class MoveComplete extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(MoveComplete.class);

    @Autowired
    private RobotPositionCache robotPositionCache; // 로봇별 마지막 위치 저장소 (선택)

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();

        RobotMaster robot = baseService.findByEntity(RobotMaster.class, new RobotMaster(robotId, siteId));
        TransferControl transfer = baseService.findByEntity(TransferControl.class, new TransferControl(robot.getTransfer_id(),siteId));

        if(CommonUtils.isNullOrEmpty(robot) || CommonUtils.isNullOrEmpty(transfer)) {
            logger.warn("robot not found");
            return BaseConstants.RETURNCODE.Fail;
        }

        NodeMaster sourceNode = RuntimeUtils.getSourceNodeFromTransfer(transfer, baseService);
        NodeMaster destNode = RuntimeUtils.getDestNodeFromTransfer(transfer, baseService);

        Position robotPos = robotPositionCache.get(robotId);
        Position sourcePos = new Position(ConvertUtils.toDouble(sourceNode.getPos_x_val()), ConvertUtils.toDouble(sourceNode.getPos_y_val()));
        Position destPos = new Position(ConvertUtils.toDouble(destNode.getPos_x_val()), ConvertUtils.toDouble(destNode.getPos_y_val()));

        //로봇의 현재 position과 source, dest Node 비교 후 const로 정의한 오차범위보다 크면 실패처리, Loading, Unloading 상태변경
        double POSITION_TOLERANCE = ConvertUtils.toDouble(baseConstantCache.get(siteId, BaseConstants.ConstantsCache.ConstType.SYSTEM, BaseConstants.ConstantsCache.ConstCode.POSITION_TOLERANCE).getConstant_val());

        boolean isLoadingAt = CommonUtils.isNullOrEmpty(transfer.getLoad_end_at());
        if(isLoadingAt)
        {
            if(isOverTolerance(robotPos.getX(), robotPos.getY(), sourcePos.getX(), sourcePos.getY(), POSITION_TOLERANCE))
            {
                logger.info("MoveComplete Source 검증 실패: Transfer [{}], Robot [{}] 위치 mismatch! 현재 pos=({}, {}), source=({}, {})",
                        transfer.getTransfer_id(), robotId, robotPos.getX(), robotPos.getY(), sourcePos.getX(), sourcePos.getY());
                return BaseConstants.RETURNCODE.Fail;
            }
            updateRobotLocation(robot, sourceNode, robotPos);
        }
        else if(!isLoadingAt)
        {
            if (isOverTolerance(robotPos.getX(), robotPos.getY(), destPos.getX(), destPos.getY(), POSITION_TOLERANCE))
            {
                logger.info("MoveComplete Destination 검증실패 : Transfer [{}], Robot {} 위치 mismatch! 현재 pos=({}, {}), dest=({}, {})",
                        transfer.getTransfer_id(), robotId, robotPos.getX(), robotPos.getY(), destPos.getX(), destPos.getY());
                return BaseConstants.RETURNCODE.Fail;
            }

            updateRobotLocation(robot, destNode, robotPos);
        }
        else
        {
            logger.error("loading time error transferId : ({})",transfer.getTransfer_id());
            return BaseConstants.RETURNCODE.Fail;
        }

        updateTransferring_subState(transfer, BaseConstants.TRANSFER.SUB_STATE.RUN_COMPLETE);

        return result;
    }

    private void updateTransferring_subState(TransferControl transfer, String subState)
    {
        transfer.setSub_status_tx(subState);
        baseService.saveOrUpdate(eventInfo, transfer);
        logger.info("updateTransfer : {} , subState: subState={} ",transfer.getTransfer_id(), subState);
    }

    private boolean isOverTolerance(double robotX, double robotY, double targetX, double targetY, double tolerance) {
        double deltaX = robotX - targetX;
        double deltaY = robotY - targetY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        logger.info("허용오차범위 : {:.2f}mm, 실제 오차 범위 : {:.2f}mm", tolerance, distance);
        return distance > tolerance;
    }

    private void updateRobotLocation(RobotMaster robot, NodeMaster targetNode, Position robotPos)
    {
        // 1. 로봇 위치 필드 갱신
        robot.setLocation_nm(targetNode.getNode_nm());

        // 2. 필요 시 로봇의 zone 정보 갱신 (현재 위치 기준 영역 외 점유정보 해제)
        long mapUuid = robot.getMap_uuid();
        String siteId = eventInfo.getSiteId();
        String robotId = robot.getRobot_id();

        // 현재 로봇이 속한 zone을 다시 계산
        var zones = globalZoneManager.getZonesByMap(mapUuid);
        for (GlobalZone zone : zones) {
            boolean isInZone = zone.contains(robotPos);

            if (!isInZone) {
                // 해당 zone을 점유
                zoneLockManager.release(siteId, zone.getZoneId(), robotId);
                logger.info("[{}] Zone [{}] 점유 해제됨 (updateRobotLocation)", robotId, zone.getZoneId());
            }
        }

        // 3. DB 반영
        baseService.update(eventInfo, robot);

        writerService.sendToUiLocationChange(eventInfo, result, targetNode.getNode_nm());

        logger.info("MoveComplete 성공: Robot {} 위치 = ({}, {}), node = {}",
                robot.getRobot_id(), robotPos.getX(), robotPos.getY(), targetNode.getNode_id());
    }
}
