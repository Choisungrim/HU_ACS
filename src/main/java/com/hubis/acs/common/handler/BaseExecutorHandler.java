package com.hubis.acs.common.handler;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.utils.CommonUtils;
import com.hubis.acs.common.utils.EventInfoBuilder;
import com.hubis.acs.common.utils.JsonUtils;
import com.hubis.acs.common.utils.TimeUtils;
import com.hubis.acs.repository.dao.CommonDAO;
import com.hubis.acs.service.WriterService;
import jakarta.annotation.Resource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.SQLSyntaxErrorException;

@Component
public class BaseExecutorHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseExecutorHandler.class);

    private final CommonDAO commonDAO;
    private final ApplicationContext appContext;
    private final WriterService writerService;

    @Autowired
    public BaseExecutorHandler(@Qualifier("commonDAO") CommonDAO commonDAO, ApplicationContext appContext, WriterService writerService) {
        this.commonDAO = commonDAO;
        this.appContext = appContext;
        this.writerService = writerService;
    }

    @Value("${system.siteid}")
    private String siteId;

    public void executeByUI(JSONObject reqMsg)
    {
        JSONObject replyMsg = new JSONObject();

        //Header
        JSONObject header = JsonUtils.getJsonObject(reqMsg, BaseConstants.TAG_NAME.Header);
        JSONObject dataSet = JsonUtils.getJsonObject(reqMsg, BaseConstants.TAG_NAME.DataSet);

        String requestId = JsonUtils.getJsonString(header, BaseConstants.TAG_NAME.RequestId);
        String workId = JsonUtils.getJsonString(header, BaseConstants.TAG_NAME.WorkId);
        String transactionId = JsonUtils.getJsonString(header, BaseConstants.TAG_NAME.TransactionId);

        String workGroupId = requestId.toLowerCase();

        if (CommonUtils.isNullOrEmpty(transactionId))
            transactionId = TimeUtils.getCurrentTimekey();

        EventInfo eventInfo = new EventInfoBuilder(transactionId)
                .addSiteId(this.siteId)
                .addRequestId(requestId)
                .addWorkId(workId)
                .addWorkGroupId(workGroupId)
                .addActivity(workId)
                .addUserId(requestId)
                .build();


        BaseLogHandler.exec(eventInfo, reqMsg, BaseConstants.EVENTLOG.LOG_TYPE.UI_Message);
        if(workId.equals(BaseConstants.UI.MESSAGENAME.UIResponse))
            ResponseHandler.completeResponse(
                    dataSet.getString("WORKID"), transactionId, dataSet.getString(BaseConstants.TAG_NAME.ReturnCode));
//
        String returnCode = this.execute(eventInfo, reqMsg, replyMsg);

    }

    public void executeByACS(JSONObject reqMsg)
    {
        JSONObject replyMsg = new JSONObject();

        //Header
        JSONObject header = JsonUtils.getJsonObject(reqMsg, BaseConstants.TAG_NAME.Header);
        JSONObject dataSet = JsonUtils.getJsonObject(reqMsg, BaseConstants.TAG_NAME.DataSet);

        String requestId = JsonUtils.getJsonString(header, BaseConstants.TAG_NAME.RequestId);
        String workId = JsonUtils.getJsonString(header, BaseConstants.TAG_NAME.WorkId);
        String transactionId = JsonUtils.getJsonString(header, BaseConstants.TAG_NAME.TransactionId);
        String userId = JsonUtils.getJsonString(header, BaseConstants.TAG_NAME.UserId);
        String siteId = JsonUtils.getJsonString(header, BaseConstants.TAG_NAME.SiteId);

        String workGroupId = requestId.toLowerCase();

        if (CommonUtils.isNullOrEmpty(transactionId))
            transactionId = TimeUtils.getCurrentTimekey();

        EventInfo eventInfo = new EventInfoBuilder(transactionId)
                .addSiteId(this.siteId)
                .addRequestId(requestId)
                .addWorkId(workId)
                .addWorkGroupId(workGroupId)
                .addActivity(workId)
                .addUserId(userId)
                .addSiteId(siteId)
                .build();

        BaseLogHandler.exec(eventInfo, reqMsg, BaseConstants.EVENTLOG.LOG_TYPE.UI_Message);
        String returnCode = this.execute(eventInfo, reqMsg, replyMsg);

        if (!returnCode.equals(BaseConstants.RETURNCODE.Success))
            BaseLogHandler.exec(eventInfo, replyMsg, BaseConstants.EVENTLOG.LOG_TYPE.UI_Error);
    }

    public String execute(EventInfo eventInfo, JSONObject reqMsg, JSONObject repMsg) {
        String returnCode = "";
        String returnMessage = "";
        Object[] returnArguments = null;
        JSONObject reqHeader = JsonUtils.getMessageObject(reqMsg, BaseConstants.TAG_NAME.Header);
        //GlobalWorkHandlerIF ruleService = BaseWorkClassLoader.getWorkObject(eventInfo.getWorkGroupId(), eventInfo.getWorkId().toLowerCase());
        GlobalWorkHandlerIF ruleService = BaseWorkHandlerRegistry.getHandler(eventInfo.getWorkGroupId().toLowerCase(), eventInfo.getWorkId().toLowerCase().toLowerCase());

        if (!CommonUtils.isNullOrEmpty(ruleService))
        {
            logger.info("▶▶▶ START [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◀◀◀ \n");
            long startTime = System.currentTimeMillis();

            try
            {

//				ruleService.doInit(appContext, commonDAO, transactionManager, eventInfo);
                ruleService.doInit(appContext, commonDAO, eventInfo);
                returnCode = ruleService.doWork(reqMsg);

            }
            catch(CustomException ce)
            {
                returnCode = ce.getErrorCode();
                returnArguments = ce.getErrorArguments();

            }
            catch(Exception e)
            {
                //For Debug Code BJJUNG
                if(e instanceof DataAccessException) {
                    SQLException se = (SQLException) ((DataAccessException) e).getRootCause();

                    logger.info("****** DataAccessException : {} // {}", se.getErrorCode(), se.getMessage());
                    logger.info("▷▷▷ DB ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
                }
                else if(e instanceof SQLSyntaxErrorException) {
                    SQLException se = ((SQLSyntaxErrorException)e).getNextException();

                    logger.info("******  SQLSyntaxErrorException : {}", se.getErrorCode());
                    logger.info("▷▷▷ DB ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
                }
                else if(e instanceof SQLInvalidAuthorizationSpecException) {
                    SQLException se = ((SQLInvalidAuthorizationSpecException) e).getNextException();

                    logger.info("****** InvalidResultSetAccessException : {}", se.getErrorCode());
                    logger.info("▷▷▷ DB ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
                }
                else if(e instanceof DuplicateKeyException) {
                    logger.info("****** DuplicateKeyException : {}", e.getMessage());
                    logger.info("▷▷▷ DB ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
                }
                else if(e instanceof DataIntegrityViolationException) {
                    logger.info("******  DataIntegrityViolationException : {}", e.getMessage());
                    logger.info("▷▷▷ DB ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
                }
                else if(e instanceof DataAccessResourceFailureException) {
                    logger.info("******  DataAccessResourceFailureException : {}", e.getMessage());
                    logger.info("▷▷▷ DB ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
                }
                else if(e instanceof CannotAcquireLockException) {
                    logger.info("******  CannotAcquireLockException : {}", e.getMessage());
                    logger.info("▷▷▷ DB ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
                }
                else if(e instanceof DeadlockLoserDataAccessException) {
                    logger.info("******  DeadlockLoserDataAccessException : {}", e.getMessage());
                    logger.info("▷▷▷ DB ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
                }
                else if(e instanceof CannotSerializeTransactionException) {
                    logger.info("******  CannotSerializeTransactionException : {}", e.getMessage());
                    logger.info("▷▷▷ DB ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
                }
//				transactionManager.rollback();

                logger.error( "Error Code", e );

                returnCode = BaseConstants.RETURNCODE.DBCommitFail;
            }

            long endTime = System.currentTimeMillis();
            logger.info("▶▶▶ FINISH [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "], ReturnCode=" + returnCode +" ElapsedTime=" + (endTime - startTime) + "ms ◀◀◀");

            if( eventInfo.getWorkGroupId().toLowerCase().equals(BaseConstants.TAG_NAME.UI))
                this.notifyUIResponse(eventInfo, returnCode);
        }
        else
        {
            logger.info("▷▷▷ ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));

            returnCode = "COM_ERR_013";
            returnArguments = new Object[] { eventInfo.getWorkId() };
        }

        return returnCode;
    }
    private void notifyUIResponse(EventInfo eventInfo, String returnCode) {
        writerService.sendToUIResponse(eventInfo, returnCode);
    }
}
