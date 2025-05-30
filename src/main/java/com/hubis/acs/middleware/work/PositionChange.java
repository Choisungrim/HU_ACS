package com.hubis.acs.middleware.work;

import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.ui.work.CreateTransferControl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_position_change")
public class PositionChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(PositionChange.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        System.out.println("PositionChange doWork");
        return result;
    }
}
