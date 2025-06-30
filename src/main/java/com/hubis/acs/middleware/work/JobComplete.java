package com.hubis.acs.middleware.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.TimeUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_jobcomplete")
public class JobComplete extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(JobComplete.class);

    @Override
    public String doWork(JSONObject message) throws Exception {

        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();

        RobotMaster robot = baseService.findByEntity(RobotMaster.class, new RobotMaster(robotId, siteId));
        TransferControl transfer = baseService.findByEntity(TransferControl.class, new TransferControl(robot.getTransfer_id(),siteId));

        robot.setStatus_tx(BaseConstants.ROBOT.STATE.IDLE);
        robot.setTransfer_id("");
        baseService.saveOrUpdate(eventInfo, robot);

        transfer.setTransfer_status_tx(BaseConstants.TRANSFER.STATE.COMPLETED);
        transfer.setSub_status_tx(BaseConstants.TRANSFER.STATE.COMPLETED);
        transfer.setJob_complete_at(TimeUtils.getLocalDateCurrentTime());
        eventInfo.setActivity("saveComplete");
        baseService.saveOrUpdate(eventInfo, transfer);

        eventInfo.setActivity("deleteComplete");
        baseService.delete(eventInfo, transfer);

        logger.info("Job complete succesful");
        return result;
    }
}