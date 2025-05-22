package com.hubis.acs.ui.work;

import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("ui_create_transfer_control")
public class CreateTransferControl extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(CreateTransferControl.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        System.out.println(message.toString());
        JSONObject header = message.getJSONObject("header");
        JSONObject dataSet = message.getJSONObject("dataSet");
        String transferId = dataSet.getString("transferId");

        String transferDest = dataSet.optString("transferDestination", "");
        String transferSource = dataSet.optString("transferSource", "");
        String transferRobot = dataSet.optString("transferRobot", "");
        int transferPriority = dataSet.optInt("transferPriority", 10);

        TransferControl transfer = new TransferControl(transferId, eventInfo.getSiteId());
        transfer.setAssigned_robot_id("");
        transfer.setPriority_no(transferPriority);
        transfer.setSource_port_id(transferSource);
        transfer.setDestination_port_id(transferDest);
        
//        baseService.save();
        baseService.saveOrUpdate(eventInfo, transfer);
        return result;
    }
}
