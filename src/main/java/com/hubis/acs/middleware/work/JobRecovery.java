package com.hubis.acs.middleware.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.entity.vo.RobotMasterId;
import com.hubis.acs.common.entity.vo.TransferControlId;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.TimeUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_job_recovery")
public class JobRecovery  extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(JobRecovery.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();
        String failedTaskName = message.getString("failedTask");

        RobotMaster robot = baseService.findById(RobotMaster.class, new RobotMasterId(robotId, siteId));
        TransferControl transfer = baseService.findById(TransferControl.class, new TransferControlId(robot.getTransfer_id(),siteId));

        robot.setStatus_tx(BaseConstants.ROBOT.STATE.IDLE);
        robot.setTransfer_id("");
        baseService.saveOrUpdate(eventInfo, robot);

        transfer.setTransfer_status_tx(BaseConstants.TRANSFER.STATE.QUEUED);  // 또는 RETRY_READY, ABORTED 등
        transfer.setSub_status_tx(failedTaskName + "_FAIL");
        baseService.saveOrUpdate(eventInfo, transfer);

        logger.info("Job recovery succesful");
        return result;
    }
}