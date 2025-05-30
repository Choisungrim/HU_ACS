package com.hubis.acs.middleware.work;

import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_battery_change")
public class BatteryChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(BatteryChange.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        System.out.println("BatteryChange doWork");
        return result;
    }
}
