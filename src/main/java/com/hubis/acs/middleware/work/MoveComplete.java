package com.hubis.acs.middleware.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.NodeMaster;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.position.cache.RobotPositionCache;
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

        //로봇의 현재 position과 source, dest Node 비교 후 Loading, Unloading 상태변경
        // EPT +- 25 mm , OMRON +-

        // 허용 오차 (mm)
        final double POSITION_TOLERANCE = 20.0;

        boolean isAtSource = isWithinTolerance(robotPos.getX(), robotPos.getY(), sourcePos.getX(), sourcePos.getY(), POSITION_TOLERANCE);
        boolean isAtDest = isWithinTolerance(robotPos.getX(), robotPos.getY(), destPos.getX(), destPos.getY(), POSITION_TOLERANCE);

        //TODO : load time / unload time 별 구분 후 목적지 위치 검증으로 수정
        if (!(isAtSource || isAtDest)) {
            logger.error("MoveComplete 검증 실패: Robot {} 위치 mismatch! 현재 pos=({}, {}), source=({}, {}), dest=({}, {})",
                    robotId, robotPos.getX(), robotPos.getY(), sourcePos.getX(), sourcePos.getY(), destPos.getX(), destPos.getY());
            return BaseConstants.RETURNCODE.Fail;
        }
        return result;
    }

    private boolean isWithinTolerance(double robotX, double robotY, double targetX, double targetY, double tolerance) {
        double deltaX = robotX - targetX;
        double deltaY = robotY - targetY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        System.out.println(distance);
        return distance <= tolerance;
    }
}
