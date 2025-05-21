package com.hubis.acs.service;

import com.hubis.acs.common.adapter.mqtt.Publisher;
import com.hubis.acs.common.constants.BaseConstants;
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

    public void sendToUIResponse(String workId, String transactionId, String returnCode, String siteId) {
        JSONObject message = new JSONObject();
        JSONObject header = new JSONObject();
        String topic = "web/response";

        header.put(BaseConstants.TAG_NAME.RequestId, BaseConstants.TAG_NAME.UI);
        header.put(BaseConstants.TAG_NAME.WorkId, workId);
        header.put(BaseConstants.TAG_NAME.TransactionId, transactionId);
        header.put(BaseConstants.TAG_NAME.SiteId, siteId);
        header.put(BaseConstants.TAG_NAME.ReturnCode, returnCode);

        message.put(BaseConstants.TAG_NAME.Header, header);
        message.put(BaseConstants.TAG_NAME.DataSet, new JSONObject());

        publisher.publish(topic, message.toString());
    }
}
