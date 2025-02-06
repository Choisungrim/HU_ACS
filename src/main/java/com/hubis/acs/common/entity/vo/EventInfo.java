package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class EventInfo {
    private String RequestId;
    private String WorkId;
    private String WorkGroupId;
    private String SiteId;
    private String Language;
    private String UserId;
    private String TimeKey;
    private Date Time;
    private String TransactionId;
    private String Activity;
    private String Comments;

    public EventInfo()
    {
        RequestId = "";
        WorkId = "";
        WorkGroupId = "";
        SiteId = "";
        Language = "";
        UserId = "";
        TimeKey = "";
        Time = null;
        TransactionId = "";
        Activity = "";
        Comments = "";
    }

    public EventInfo clone()
    {
        EventInfo cloneEventInfo = new EventInfo();

        cloneEventInfo.setRequestId(this.RequestId);
        cloneEventInfo.setWorkId(this.WorkId);
        cloneEventInfo.setWorkGroupId(this.WorkGroupId);
        cloneEventInfo.setSiteId(this.SiteId);
        cloneEventInfo.setLanguage(this.Language);
        cloneEventInfo.setUserId(this.UserId);
        cloneEventInfo.setTimeKey(this.TimeKey);
        cloneEventInfo.setTime(this.Time);
        cloneEventInfo.setTransactionId(this.TransactionId);
        cloneEventInfo.setActivity(this.Activity);
        cloneEventInfo.setComments(this.Comments);

        return cloneEventInfo;
    }
}
