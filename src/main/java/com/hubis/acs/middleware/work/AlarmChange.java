package com.hubis.acs.middleware.work;

import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_alarm_change")
public class AlarmChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(AlarmChange.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        System.out.println("AlarmChange doWork");
        return result;
    }
}
