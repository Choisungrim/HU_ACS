package com.hubis.acs.common.work;

import com.hubis.acs.common.adapter.mqtt.Publisher;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class feedback extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(feedback.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        publisher.publish("your/topicfeedback", "456");
        return result;
    }
}
