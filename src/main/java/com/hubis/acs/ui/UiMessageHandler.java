package com.hubis.acs.ui;

import com.hubis.acs.common.handler.BaseExecutorHandler;
import com.hubis.acs.common.utils.JsonUtils;
import com.hubis.acs.middleware.MiddlewareMessageHandler;
import com.hubis.acs.service.WriterService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UiMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(UiMessageHandler.class);

    private final BaseExecutorHandler executor;
    private final WriterService writerService;

    public void handle(String topic, Message<?> msg)
    {
        if(topic.equals("web/backend/connection/request"))
            processUIHeartbeatMessage(msg);
        else
            processUIMessages(msg);
    }

    private void processUIMessages(Message<?> message)
    {
        try {
            JSONObject reqMsg = JsonUtils.validationMessageToJsonObject(message);

            executor.executeByACS(reqMsg);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void processUIHeartbeatMessage(Message<?> message)
    {
        try {
            JSONObject reqMsg = JsonUtils.validationMessageToJsonObject(message);
            writerService.sendToUIHeartbeat(reqMsg);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
