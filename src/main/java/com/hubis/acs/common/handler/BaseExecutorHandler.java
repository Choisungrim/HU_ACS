package com.hubis.acs.common.handler;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.utils.CommonUtils;
import com.hubis.acs.common.utils.EventInfoBuilder;
import com.hubis.acs.common.utils.JsonUtils;
import com.hubis.acs.common.utils.TimeUtils;
import com.hubis.acs.repository.dao.CommonDAO;
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

    @Autowired
    public BaseExecutorHandler(@Qualifier("commonDAO") CommonDAO commonDAO, ApplicationContext appContext) {
        this.commonDAO = commonDAO;
        this.appContext = appContext;
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
//        if(workId.equals(UI.MESSAGENAME.UIResponse))
//            ResponseHandler.completeResponse(
//                    dataSet.getString("WORKID"), transactionId, dataSet.getString(BaseConstants.TAG_NAME.ReturnCode));
//        else
//        {
            String returnCode = this.execute(eventInfo, reqMsg, replyMsg);
//
//            if (!returnCode.equals(BaseConstants.RETURNCODE.Success))
//                LogHandler.exec(eventInfo, replyMsg, BaseConstants.EVENTLOG.LOG_TYPE.UI_Error);
//        }
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
        String returnCode = this.execute(eventInfo, reqMsg, replyMsg);

        if (!returnCode.equals(BaseConstants.RETURNCODE.Success))
            BaseLogHandler.exec(eventInfo, replyMsg, BaseConstants.EVENTLOG.LOG_TYPE.UI_Error);
    }

    public String execute(EventInfo eventInfo, JSONObject reqMsg, JSONObject repMsg) {
        String returnCode = "";
        String returnMessage = "";
        Object[] returnArguments = null;
        JSONObject reqHeader = JsonUtils.getMessageObject(reqMsg, BaseConstants.TAG_NAME.Header);
        GlobalWorkHandlerIF ruleService = BaseWorkClassLoader.getWorkObject(eventInfo.getWorkGroupId(), eventInfo.getWorkId().toLowerCase());

        if (!CommonUtils.isNullOrEmpty(ruleService))
        {
            logger.info("▶▶▶ START [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◀◀◀ \n" + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));
            long startTime = System.currentTimeMillis();

            try
            {
//				transactionManager.begin();

//				ruleService.doInit(appContext, commonDAO, transactionManager, eventInfo);
                ruleService.doInit(appContext, commonDAO, eventInfo);
                returnCode = ruleService.doWork(reqMsg);

//		    	transactionManager.commit();
            }
            catch(CustomException ce)
            {
                returnCode = ce.getErrorCode();
                returnArguments = ce.getErrorArguments();

                // 여러 건 부분 에러 시, CustomException 발생한다.
//				if (returnCode.equals(BaseConstants.RETURNCODE.PartialError))
//				{
//					transactionManager.commit();
//				}
//				else
//				{
//					transactionManager.rollback();
//				}
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

//            if( eventInfo.getWorkGroupId().toUpperCase().equals("UI"))
//                this.notifyUIResponse(returnCode, reqHeader.getString(BaseConstants.TAG_NAME.TransactionId), eventInfo.getSiteId(), eventInfo.getWorkId());
        }
        else
        {
            logger.info("▷▷▷ ERROR [" + eventInfo.getRequestId() + "." + eventInfo.getWorkId() + "." + eventInfo.getTransactionId() + "] ◁◁◁ " + JsonUtils.getIndentedStyle(JsonUtils.toString(reqMsg)));

            returnCode = "COM_ERR_013";
            returnArguments = new Object[] { eventInfo.getWorkId() };
        }

//        try
//        {
//            returnMessage = NlsCache.getNlsData(eventInfo.getSiteId(), returnCode, eventInfo.getLanguage());
//
//            if (!CommonUtils.isNullOrEmpty(returnArguments))
//                returnMessage = NlsCache.getNlsData(eventInfo.getSiteId(), returnCode, eventInfo.getLanguage(), returnArguments);
//
//            // Error 시, ReturnCode 같이 보여주자.
//            if (!returnCode.equals(BaseConstants.RETURNCODE.Success))
//                returnMessage = "[" + returnCode + "]\n" + returnMessage;
//
//            JsonUtils.putJsonObject(repMsg, BaseConstants.TAG_NAME.ReturnCode, returnCode);
//            JsonUtils.putJsonObject(repMsg, BaseConstants.TAG_NAME.ReturnMessage, returnMessage);
//        }
//        catch (Exception e) {}

        return returnCode;
    }

}
