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
        public final static String Task = "task";
        public final static String Response = "response";
        public final static String Request = "request";
    }

    public static class RETURNCODE
    {
        public final static String Success = "0";
        public final static String Fail = "-1";
        public final static String UndefinedError = "-999";
        public final static String DBCommitFail = "DB_COMMIT_FAIL";
    }

    public static class RequestId
    {
        public final static String ACS = "acs";
        public final static String MiddleWare = "middleware";
        public final static String UI = "ui";
        public final static String MES = "mes";
        public final static String MCS = "mcs";
        public final static String WMS = "wms";
        public final static String WCS = "wcs";
    }

    public static class ROBOT
    {
        public static class Task
        {
            public final static String MOVE = "move";
            public final static String LOAD = "load";
            public final static String UNLOAD = "unload";
            public static class State {
                public final static String Move_Start = "move_start";
                public final static String Move_Complete = "move_complete";
                public final static String Load_Start = "load_start";
                public final static String Load_Complete = "load_complete";
                public final static String Unload_Start = "unload_start";
                public final static String Unload_Complete = "unload_complete";
                public final static String Job_Complete = "job_complete";

            }
        }

        public static class STATE
        {
            public final static String RUNNING = "running";
            public final static String LOADING = "loading";
            public final static String UNLOADING = "unloading";

        }
    }



    public static class Language
    {
        public final static String English = "en";
        public final static String Spanish = "es";
        public final static String Korean = "ko";
        public final static String China = "ch";
        public final static String Japan = "jp";

    }



    public static class Transfer
    {
        public static class State
        {
            public final static String READY = "ready";
            public final static String QUEUED = "queued";
            public final static String RUNNING = "running";
            public final static String LOADING = "loading";
            public final static String UNLOADING = "unloading";
            public final static String COMPLETED = "completed";
        }
    }

    public static class EVENTLOG
    {
        public static class LOG_TYPE
        {
            public final static String UI_Message = "UI_Message";
            public final static String UI_Error = "UI_Error";
        }
    }

    public static class Usable
    {
        public final static int USABLE = 1;
        public final static int UNUSABLE = 0;
    }
    public static class Cache
    {
        public static class ConstType
        {
            public final static String WORK = "WORK";
        }
        public static class ConstCode
        {
            public final static String WORKABLE_ROBOT_BATTERY = "WORKABLE_ROBOT_BATTERY";
        }
    }

    public static class Zone
    {
        public static class ZoneType
        {
            public final static String BLOCK = "block";

        }
    }
}
