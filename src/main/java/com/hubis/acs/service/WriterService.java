package com.hubis.acs.service;

import com.hubis.acs.common.adapter.mqtt.Publisher;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.position.model.Position;
import com.hubis.acs.common.utils.EventInfoBuilder;
import com.hubis.acs.common.utils.JsonUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WriterService {

    private final Publisher publisher;

    public WriterService(Publisher publisher) {
        this.publisher = publisher;
    }

    private JSONObject makeHeader(EventInfo eventInfo) {
        JSONObject header = new JSONObject();
        header.put(BaseConstants.TAG_NAME.RequestId, BaseConstants.TAG_NAME.ACS);
        header.put(BaseConstants.TAG_NAME.WorkId, eventInfo.getWorkId());
        header.put(BaseConstants.TAG_NAME.TransactionId, eventInfo.getTransactionId());
        header.put(BaseConstants.TAG_NAME.SiteId, eventInfo.getSiteId());
        return header;
    }

    public void sendToUIResponse(EventInfo eventInfo, String returnCode) {
        JSONObject msg = new JSONObject();
        String topic = "web/response";

        JSONObject header = makeHeader(eventInfo);
        header.put(BaseConstants.TAG_NAME.ReturnCode, returnCode);

        msg.put(BaseConstants.TAG_NAME.Header, header);
        msg.put(BaseConstants.TAG_NAME.DataSet, new JSONObject());

        publisher.publish(topic, msg.toString());
    }

    public void sendToUIPositionChange(EventInfo eventInfo, String returnCode, Position position) {
        JSONObject msg = new JSONObject();
        String topic = "web/backend/event/position_change";

        JSONObject header = makeHeader(eventInfo);
        header.put(BaseConstants.TAG_NAME.ReturnCode, returnCode);

        JSONObject dataSet = new JSONObject();
        dataSet.put("x", position.getX());
        dataSet.put("y", position.getY());
        dataSet.put("deg", position.getTheta());

        msg.put(BaseConstants.TAG_NAME.Header, header);
        msg.put(BaseConstants.TAG_NAME.DataSet, dataSet);


        publisher.publish(topic, msg.toString());
    }

    public void sendToUIHeartbeat(JSONObject reqMsg) {
        JSONObject msg = new JSONObject();
        String topic = "web/backend/connection/response";

        JSONObject responseHeader = reqMsg.getJSONObject(BaseConstants.TAG_NAME.Header);
        EventInfo eventInfo = new EventInfoBuilder()
                .addTransactionId(responseHeader.optString(BaseConstants.TAG_NAME.TransactionId))
                .addWorkId(responseHeader.optString(BaseConstants.TAG_NAME.WorkId))
                .addSiteId(responseHeader.optString(BaseConstants.TAG_NAME.SiteId))
                .build();

        JSONObject header = makeHeader(eventInfo);

        JSONObject data = new JSONObject();
        data.put("available", true);
        msg.put(BaseConstants.TAG_NAME.Header, header);
        msg.put(BaseConstants.TAG_NAME.DataSet, data);

        publisher.publish(topic, msg.toString());
    }

    public void sendToMiddlewareHeartbeat(JSONObject reqMsg, String robotId) {

        StringBuilder topics = new StringBuilder();
        topics.append("middleware/");
        topics.append(robotId);
        topics.append("/connection/request");

        publisher.publish(topics.toString(), reqMsg.toString());
    }

    public void sendToJsonMiddleware(String txId, String task, String topic, String taskId, String robotId, String destination) {
        JSONObject message = new JSONObject();

        message.put("tid", txId);
        message.put("update_time", txId);
        message.put("task_system", "");
        message.put("task_id", taskId);
        message.put("task_behavior", task);
        message.put("task_to",destination);

        publisher.publish(topic, message.toString());
    }

    public void sendTopic(EventInfo eventInfo, String topic, String message) {
        JSONObject header = makeHeader(eventInfo);
        JSONObject msg = new JSONObject();

        msg.put(BaseConstants.TAG_NAME.Header, header);
        msg.put(BaseConstants.TAG_NAME.DataSet, JsonUtils.toJson(message));

        publisher.publish(topic, msg.toString());
    }
}
