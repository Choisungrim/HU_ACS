package com.hubis.acs.service;

import com.hubis.acs.common.cache.BaseConstantCache;
import com.hubis.acs.common.configuration.thread.AsyncConfig;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.*;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.entity.vo.NodeMasterId;
import com.hubis.acs.common.entity.vo.PortMasterId;
import com.hubis.acs.common.utils.CommonUtils;
import com.hubis.acs.common.utils.EventInfoBuilder;
import com.hubis.acs.common.utils.RuntimeUtils;
import com.hubis.acs.common.utils.TimeUtils;
import com.hubis.acs.process.ProcessManager;
import com.hubis.acs.repository.RobotMasterRepository;
import com.hubis.acs.repository.TransferControlRepository;
import com.hubis.acs.scheduler.TransferTaskScheduler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TransferControlRepository transferRepository;
    private final RobotMasterRepository robotMasterRepository;
    private final BaseService baseService;
    private final WriterService writerService;
    private final AsyncConfig asyncConfig;
    private final BaseConstantCache baseConstantCache;
    private final ProcessManager processManager;


    public TaskService(TransferControlRepository transferRepository,
                       RobotMasterRepository robotMasterRepository,
                       BaseService baseService, WriterService writerService,
                       AsyncConfig asyncConfig, BaseConstantCache baseConstantCache,
                       ProcessManager processManager) {
        this.transferRepository = transferRepository;
        this.robotMasterRepository = robotMasterRepository;
        this.baseService = baseService;
        this.writerService = writerService;
        this.asyncConfig = asyncConfig;
        this.baseConstantCache = baseConstantCache;
        this.processManager = processManager;
    }

    public void assignReadyTransfers(String siteCd) {
        List<TransferControl> transfers = transferRepository.findReadyTransfers(siteCd);
        TaskExecutor executor = asyncConfig.getOrCreateExecutor(getClass().getName()+"-"+siteCd, 5);

        for (TransferControl transfer : transfers) {
            executor.execute(() -> assignTransferToRobot(transfer));
        }
    }

    private void assignTransferToRobot(TransferControl transfer) {
        EventInfo eventInfo = new EventInfoBuilder(TimeUtils.getCurrentTimekey())
                .addSiteId(transfer.getSite_cd())
                .addActivity(getClass().getSimpleName())
                .addRequestId(BaseConstants.RequestId.ACS)
                .addLanguage(BaseConstants.Language.Korean)
                .build();

        NodeMaster sourceNode = RuntimeUtils.getSourceNodeFromTransfer(transfer, baseService);
        NodeMaster destNode = RuntimeUtils.getDestNodeFromTransfer(transfer, baseService);
        if (sourceNode == null || destNode == null) return;

        Point2D sourcePoint = parsePoint(sourceNode.getPos_x_val(), sourceNode.getPos_y_val());
        if (sourcePoint == null) {
            System.out.println("[ERROR] Source 좌표 파싱 실패");
            return;
        }

        List<RobotMaster> availableRobots = new ArrayList<>();
        if(CommonUtils.isNullOrEmpty(transfer.getAssigned_robot_id()))
            // 로봇 정보가 비어있는 경우,
            availableRobots = robotMasterRepository.findAvailableRobots(transfer.getSite_cd());
        else
            // 로봇 정보가 지정된 경우
            availableRobots = robotMasterRepository.findAvailableRobotById(transfer.getAssigned_robot_id(),transfer.getSite_cd());

        if (availableRobots.isEmpty()) {
            transerSetQueue(eventInfo,transfer);
            return;
        }


        Map<String, NodeMaster> nodeMap = getAvailableRobotsInSameMap(availableRobots, sourceNode.getMap_uuid(), transfer.getSite_cd());
        List<RobotMaster> candidateRobots = filterRobotsInNodeMap(availableRobots, nodeMap.keySet());

        if (candidateRobots.isEmpty()) {
            log.warn("[WARN] 유효한 맵에 속한 로봇 후보 없음");
            transerSetQueue(eventInfo,transfer);
            return;
        }

        RobotMaster selected = selectAndReserveRobot(candidateRobots, nodeMap, sourcePoint);

        if (selected != null) {
            transfer.setAssigned_robot_id(selected.getRobot_id());
            transfer.setTransfer_status_tx(BaseConstants.TRANSFER.STATE.TRANSFERRING);
            System.out.println("저장결과 작업 :"+baseService.saveOrUpdate(eventInfo, transfer));

            selected.setTransfer_id(transfer.getTransfer_id());
            selected.setStatus_tx(BaseConstants.ROBOT.STATE.ALLOCATED);

            System.out.println("저장결과 로봇 :"+baseService.saveOrUpdate(eventInfo, selected));
            System.out.println("[INFO] 로봇 '" + selected.getRobot_id() + "' → 작업 '" + transfer.getTransfer_id() + "' 할당 완료");

            processManager.tryStartProcess(transfer.getTransfer_id(), selected.getRobot_id(), transfer.getSite_cd(), sourceNode.getNode_id(), destNode.getNode_id());
        } else {
            System.out.println("[WARN] 로봇 선택 실패 (거리 계산 실패)");
            transerSetQueue(eventInfo,transfer);
        }
    }

    private Map<String, NodeMaster> getAvailableRobotsInSameMap(List<RobotMaster> robotlst, Long mapUuid, String siteCd) {
        ConstMaster constVal = baseConstantCache.get(siteCd, BaseConstants.ConstantsCache.ConstType.SYSTEM, BaseConstants.ConstantsCache.ConstCode.WORKABLE_ROBOT_BATTERY);
        int minBattery = constVal != null ? Integer.parseInt(constVal.getConstant_val()) : 0;

        Set<String> assignedIds = new HashSet<>(transferRepository.findAllAssignedRobotIds());
        List<RobotMaster> availableRobots = robotlst.stream()
                .filter(r -> !assignedIds.contains(r.getRobot_id()))
                .filter(r -> r.getBattery_no() > minBattery)
                .collect(Collectors.toList());

        Set<String> nodeIds = availableRobots.stream()
                .map(RobotMaster::getLocation_nm)
                .collect(Collectors.toSet());

        return nodeIds.stream()
                .map(id -> {
                    NodeMaster nodeId = new NodeMaster();
                    nodeId.setNode_id(id);
                    nodeId.setSite_cd(siteCd);
                    return baseService.findByEntity(NodeMaster.class, nodeId);
                })
                .filter(Objects::nonNull)
                .filter(n -> Objects.equals(n.getMap_uuid(), mapUuid))
                .collect(Collectors.toMap(NodeMaster::getNode_id, Function.identity()));
    }

    private List<RobotMaster> filterRobotsInNodeMap(List<RobotMaster> robotlst, Set<String> validNodeIds) {
        return robotlst.stream()
                .filter(r -> validNodeIds.contains(r.getLocation_nm()))
                .collect(Collectors.toList());
    }

    private RobotMaster selectAndReserveRobot(List<RobotMaster> candidates, Map<String, NodeMaster> nodeMap, Point2D source) {

        return candidates.stream()
                .min(Comparator.comparingDouble(r -> {
                    NodeMaster node = nodeMap.get(r.getLocation_nm());
                    Point2D p = parsePoint(node.getPos_x_val(), node.getPos_y_val());
                    return (p != null) ? getDistance(source, p) : Double.MAX_VALUE;
                }))
                .filter(r -> processManager.reserveRobot(r.getRobot_id()))
                .stream().findFirst()
                .orElse(null);
    }

    private double getDistance(Point2D p1, Point2D p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private Point2D.Double parsePoint(String x, String y) {
        try {
            return new Point2D.Double(Double.parseDouble(x), Double.parseDouble(y));
        } catch (Exception e) {
            log.warn("[ERROR] 좌표 파싱 실패: {}, {}", x, y);
            return null;
        }
    }

    private void transerSetQueue(EventInfo eventInfo, TransferControl transfer) {
        if(transfer.getTransfer_status_tx()!=BaseConstants.TRANSFER.STATE.QUEUED) {
            transfer.setTransfer_status_tx(BaseConstants.TRANSFER.STATE.QUEUED);
            baseService.saveOrUpdate(eventInfo,transfer);
        }
    }
}
