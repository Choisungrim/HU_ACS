package com.hubis.acs.service.impl;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.vo.RobotMasterId;
import com.hubis.acs.repository.RobotMasterRepository;
import com.hubis.acs.service.BaseService;
import com.hubis.acs.service.RobotService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("RobotService")
@RequiredArgsConstructor
public class RobotServiceImpl implements RobotService {
    private static final Logger log = LoggerFactory.getLogger(RobotServiceImpl.class);
    private final RobotMasterRepository robotMasterRepository;
    private final BaseService baseService;

    public void refreshRobotConnectionStatus(String robotId, String siteCd) {
        RobotMaster robots = new RobotMaster(robotId, siteCd);
        RobotMaster robot = baseService.findByEntity(RobotMaster.class, robots);
        if (robot != null && robot.getUsable_fl() == BaseConstants.Usable.UNUSABLE) {
            robot.setUsable_fl(BaseConstants.Usable.USABLE);
            robotMasterRepository.save(robot);
            log.info("Robot {} marked as usable again", robotId);
        }
        else if (robot == null)
            log.info("Not Definition Robot : {}", robotId);
    }

    public void robotDisconnectionStatus(String robotId, String siteCd) {

        RobotMasterId id = new RobotMasterId(robotId, siteCd);
        RobotMaster robot = baseService.findById(RobotMaster.class, id);

        if (robot != null && robot.getUsable_fl() == BaseConstants.Usable.USABLE) {
            robot.setUsable_fl(BaseConstants.Usable.UNUSABLE);
            robotMasterRepository.save(robot);
        }
    }

    public List<RobotMaster> findAllRobots() {
        List<RobotMaster> robotList = robotMasterRepository.findAll();
        if(!robotList.isEmpty())
            return robotList;
        return new ArrayList<>();
    }

}
