package com.hubis.acs.common.handler.impl;

import com.hubis.acs.common.adapter.mqtt.Publisher;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.handler.GlobalWorkHandlerIF;
import com.hubis.acs.repository.dao.CommonDAO;
import com.hubis.acs.service.BaseService;
import com.hubis.acs.service.WriterService;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class GlobalWorkHandler implements GlobalWorkHandlerIF {

    protected ApplicationContext appContext;
    protected CommonDAO commonDAO;
    protected EventInfo eventInfo;

    //Service
    protected BaseService baseService;
    protected WriterService writerService;

    public String result = BaseConstants.RETURNCODE.Success;

    public String doWork(JSONObject message) throws Exception
    {
        return BaseConstants.RETURNCODE.Success;
    }

    public void doInit(ApplicationContext appContext, CommonDAO commonDAO, EventInfo eventInfo) throws Exception
    {
        this.appContext = appContext;
        this.commonDAO = commonDAO;
        this.eventInfo = eventInfo;
        this.baseService = appContext.getBean("BaseService",BaseService.class);
        this.writerService = appContext.getBean("WriterService",WriterService.class);

    }
}
