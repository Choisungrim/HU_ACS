package com.hubis.acs.ui.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.TimeUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("ui_create_transfer_control")
public class CreateTransferControl extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(CreateTransferControl.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        JSONObject header = message.getJSONObject("header");
        JSONObject dataSet = message.getJSONObject("dataSet");

        String transferId = dataSet.optString("transferId", TimeUtils.getCurrentTimekey());
        String transferDest = dataSet.optString("transferDestination", "");
        String transferSource = dataSet.optString("transferSource", "");
        String transferRobot = dataSet.optString("transferRobot", "");
        int transferPriority = dataSet.optInt("transferPriority", 10);

        TransferControl transfer = new TransferControl(transferId, eventInfo.getSiteId());
        transfer.setTransfer_st(BaseConstants.Transfer.State.READY);
        transfer.setPriority_no(transferPriority);
        transfer.setSource_port_id(transferSource);
        transfer.setDestination_port_id(transferDest);
        transfer.setAssigned_robot_id(transferRobot);
        
        baseService.saveOrUpdate(eventInfo, transfer);
        return result;
    }
}
