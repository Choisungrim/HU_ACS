package com.hubis.acs.middleware.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.CommonUtils;
import com.hubis.acs.ui.work.CreateTransferControl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_load_start")
public class LoadStart extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoadStart.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();

        RobotMaster robotKey = new RobotMaster(robotId, siteId);
        RobotMaster robot = baseService.findByEntity(RobotMaster.class, robotKey);

        if(CommonUtils.isNullOrEmpty(robot)) {
            logger.warn("robot not found");
            return BaseConstants.RETURNCODE.Fail;
        }

        if(CommonUtils.isNullOrEmpty(robot.getTransfer_id()))
            logger.warn("robot Assigned Transfer not found");

        //상태 업데이트
        robot.setStatus_tx(BaseConstants.ROBOT.STATE.LOADING);
        baseService.saveOrUpdate(eventInfo,robot);
        return result;
    }
}
