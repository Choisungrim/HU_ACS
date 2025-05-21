package com.hubis.acs.common.work;

import com.hubis.acs.common.entity.LangMaster;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("work_feedback")
public class feedback extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(feedback.class);

    @Override
    public String doWork(JSONObject message) throws Exception {

        publisher.publish("itk/acs", "456");
        LangMaster lang = new LangMaster();
        lang.setSite_cd("HU");

        List<LangMaster> langList = baseService.findByConditions(LangMaster.class, lang);
        LangMaster langEntity = baseService.findByEntity(LangMaster.class, lang);

        System.out.println("langEntity = " + langEntity.toString());
        System.out.println("langEntity = " + langEntity.getLang_cd());


        System.out.println("langList = > "+langList.toString());
        for(LangMaster l : langList)
        {
            System.out.println("langList = > "+l.toString());
        }
        return result;
    }
}
