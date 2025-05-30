package com.hubis.acs.service;

import com.hubis.acs.common.entity.RobotMaster;

import java.util.List;

public interface RobotService {
    public void refreshRobotConnectionStatus(String robotId, String siteCd) ;
    public void robotDisconnectionStatus(String robotId, String siteCd);
    public List<RobotMaster> findAllRobots();
}
