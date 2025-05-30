package com.hubis.acs.middleware.work;

import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.ui.work.CreateTransferControl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_mode_change")
public class ModeChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(ModeChange.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        System.out.println("ModeChange doWork");
        return result;
    }
}
