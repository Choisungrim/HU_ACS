package com.hubis.acs.common.utils;

import com.hubis.acs.common.entity.vo.EventInfo;

import java.security.SecureRandom;
import java.util.Random;

public class EventInfoBuilder {
    private EventInfo event;

    public EventInfoBuilder () {
        this.event = new EventInfo();
        event.setTransactionId(TimeUtils.getCurrentTimekey());
        event.setTimeKey(TimeUtils.getCurrentTimekey()+makeRandomSeq());
        event.setTime(TimeUtils.getCurrentTime());
    }

    public EventInfoBuilder (String transactionId) {
        this.event = new EventInfo();
        event.setTransactionId(transactionId);
        event.setTimeKey(TimeUtils.getCurrentTimekey()+makeRandomSeq());
        event.setTime(TimeUtils.getCurrentTime());
    }

    public EventInfoBuilder addSiteId(String siteId) { event.setSiteId(siteId); return this;}
    public EventInfoBuilder addUserId(String userId ) { event.setUserId(userId); return this;}
    public EventInfoBuilder addLanguage(String language ) { event.setLanguage(language ); return this;}
    public EventInfoBuilder addWorkGroupId(String workGroupId) { event.setWorkGroupId(workGroupId); return this;}
    public EventInfoBuilder addRequestId(String requestId) { event.setRequestId(requestId); return this;}
    public EventInfoBuilder addWorkId(String workId) { event.setWorkId(workId); return this;}
    public EventInfoBuilder addComments(String comments) { event.setComments(comments); return this;}
    public EventInfoBuilder addActivity(String activity) { event.setActivity(activity); return this;}
    public EventInfoBuilder addTransactionId(String transactionId) {event.setTransactionId(transactionId); return this; }

    private static String makeRandomSeq()
    {
        String res = "000";

        try
        {
            Random random = SecureRandom.getInstanceStrong();

            int seq = random.nextInt(1000);
            res = ConvertUtils.toString(seq);

            if (seq < 10)
                res = "0" + res;

            if (seq < 100)
                res = "0" + res;
        }
        catch (Exception e) {}

        return res;
    }

    public EventInfo build() { return this.event; }
}
