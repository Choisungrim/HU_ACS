package com.hubis.acs.common.work;

import com.hubis.acs.common.adapter.mqtt.Publisher;
import com.hubis.acs.common.entity.LangMaster;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class feedback extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(feedback.class);

    @Override
    public String doWork(JSONObject message) throws Exception {

        publisher.publish("your/topicfeedback", "456");
        LangMaster lang = new LangMaster();
        lang.setSite_cd("HU");
        List<LangMaster> langList = commonDAO.selectList(LangMaster.class, lang);
        System.out.println("langList = > "+langList.toString());
        for(LangMaster l : langList)
        {
            System.out.println("langList = > "+l.toString());
        }
        return result;
    }
}
