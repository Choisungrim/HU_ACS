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
                public final static String MIDDLEWAREWork = "com.hubis.acs.middleware.work";
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
        public static class MODEL
        {

        }
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
        public static class TYPE
        {
            public static class STATE {
                public final static String STOPPED = "stopped";
                public final static String RUNNING = "running";
            }
            public static class KEY {
                public final static String LIFT_STATUS = "lift_status";
                public final static String LIFT_POSITION = "lift_position";
                public final static String CONVEYOR_STATUS = "conveyor_status";
                public final static String CONVEYOR_POSITION = "conveyor_position";
                public final static String TOWING_STATUS = "towing_status";
                public final static String TOWING_POSITION = "towing_position";
            }
            public final static String LIFT = "lift";
            public final static String CONVEYOR = "conveyor";
            public final static String TOWING = "towing";
        }

        public static class STATE
        {
            public final static String IDLE = "idle";
            public final static String ALLOCATED = "allocated";
            public final static String RUNNING = "running";
            public final static String LOADING = "loading";
            public final static String UNLOADING = "unloading";
            public final static String WAITING = "waiting";
            public final static String BLOCKING = "blocking";
            public final static String CHARGING = "charging";
            public final static String ALARM = "alarm";
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



    public static class TRANSFER
    {
        public static class STATE
        {
            public final static String READY = "ready";
            public final static String QUEUED = "queued";
            public final static String TRANSFERRING = "transferring";
            public final static String CANCELED = "canceled";
            public final static String ABORTED = "aborted";
            public final static String CHARGING = "charging";
            public final static String COMPLETED = "completed";
        }

        public static class SUB_STATE
        {
            public final static String RUNNING = "running";
            public final static String RUN_COMPLETE = "run_complete";
            public final static String LOADING = "loading";
            public final static String LOAD_COMPLETE = "load_complete";
            public final static String UNLOADING = "unloading";
            public final static String UNLOAD_COMPLETE = "unload_complete";
            public final static String WAITING = "waiting";
            public final static String BLOCKING = "blocking";
            public final static String CANCELING = "canceling";
            public final static String ABORTING = "aborting";
            public final static String PRECHARGING = "precharging";
            public final static String CHARGING = "charging";
            public final static String CHARGED = "charged";
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
    public static class ConstantsCache
    {
        public static class ConstType
        {
            public final static String SYSTEM = "SYSTEM";
            public final static String TRAFFIC = "TRAFFIC";
        }
        public static class ConstCode
        {
            public final static String POSITION_TOLERANCE = "VALIDATION_001";
            public final static String WORKABLE_ROBOT_BATTERY = "WORK_001";
            public final static String AUTOMATE_ASSIGNED = "WORK_002";
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
