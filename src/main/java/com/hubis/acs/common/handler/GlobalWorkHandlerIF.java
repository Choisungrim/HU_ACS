package com.hubis.acs.common.handler;

import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.repository.dao.CommonDAO;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

public interface GlobalWorkHandlerIF {

    String doWork(JSONObject message) throws Exception;
    void doInit(ApplicationContext appContext, CommonDAO commonDAO, EventInfo eventInfo) throws Exception;
}
