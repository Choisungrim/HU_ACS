package com.hubis.acs.common.constants;

public class BaseConstants {
    public static class SYSTEM
    {
        public static class CONFIG
        {
            public static class PACKAGE
            {
                public final static String GeneralWork = "com.hubis.acs.common.work";
                public final static String UIWork = "com.hubis.acs.ui.work";
                public final static String ACSWork = "com.hubis.acs.acs.work";
            }
        }
    }

    public static class UI
    {

        public static class MESSAGENAME
        {
            public final static String UIResponse = "ui_response";
        }
    }
    public static class TAG_NAME
    {
        public final static String Header = "header";
        public final static String DataSet = "dataSet";
        public final static String RequestId = "requestId";
        public final static String WorkId = "workId";
        public final static String TransactionId = "transactionId";
        public final static String UserId = "userId";
        public final static String ReturnCode = "returnCode";
        public final static String ReturnMessage = "returnMessage";
        public final static String SiteId = "siteId";
        public final static String UI = "ui";
        public final static String ACS = "acs";
        public final static String MiddleWare = "middleware";



    }

    public static class RETURNCODE
    {
        public final static String Success = "0";
        public final static String Fail = "-1";
        public final static String UndefinedError = "-999";
        public final static String DBCommitFail = "DB_COMMIT_FAIL";
    }

    public static class EVENTLOG
    {
        public static class LOG_TYPE
        {
            public final static String UI_Message = "UI_Message";
            public final static String UI_Error = "UI_Error";
        }
    }
}
