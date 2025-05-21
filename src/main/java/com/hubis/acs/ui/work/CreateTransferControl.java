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

        TransferControl transfer = new TransferControl();

        String transferId = dataSet.getString("transferId");
        String transferDest = dataSet.optString("transferDestination", "");
        String transferSource = dataSet.optString("transferSource", "");
        String transferRobot = dataSet.optString("transferRobot", "");
        String transferPriority = dataSet.optString("transferPriority", "");

//        baseService.save();

        return result;
    }
}
