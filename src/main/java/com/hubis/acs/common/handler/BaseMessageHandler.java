package com.hubis.acs.common.handler;

import com.hubis.acs.common.configuration.WorkQueue;
import com.hubis.acs.common.utils.CommonUtils;
import com.hubis.acs.common.utils.JsonUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class BaseMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(BaseMessageHandler.class);

    @Autowired
    BaseExecutorHandler executor;

    public BaseMessageHandler(){};

    public void handle(Message<?> msg, String client) {
        if(CommonUtils.isNullOrEmpty(client))
        {
            logger.error("MessageHandle Error => "+ msg + "Client Subscription type => "+client);
            return;
        }
        else
            processMessages(msg,client);
    }



    private void processMessages(Message<?> message, String client)
    {
        try {
            JSONObject reqMsg = JsonUtils.toJson(message.getPayload().toString());
            executor.executeByACS(reqMsg);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

}

//class commandThread implements Runnable
//{
//    private String vehicleId;
//
//    public commandThread(String VehicleId) {
//        this.vehicleId = VehicleId;
//    }
//
//    @Override
//    public void run()
//    {
//        try
//        {
//            while(true)
//            {
//                Queue<Message<?>> queue = MqttCache.getMqttVehicleQueuePoll(vehicleId);
//                Thread.sleep(1);
//
//                Message<?> jsonmsg2 = queue.poll();
//
//                if(CommonUtils.isNullOrEmpty(jsonmsg2))
//                    continue;
//
//                JSONObject queuemsg = JSONObject.fromObject(new String((String) jsonmsg2.getPayload()));
//
//                String trx_id = JsonUtils.getMessageValue(queuemsg, BaseConstants.AMR.transactionId);
//
//                EventInfo eventInfo = setEventInfo(vehicleId);
//                if(!CommonUtils.isNullOrEmpty(trx_id))
//                    eventInfo.setTransactionId(trx_id);
//
//                String queuetopics = jsonmsg2.getHeaders().get(BaseConstants.MQTT.TOPIC.ReceiveTopic).toString();
//                String[] splitTopics = queuetopics.split("/");
//                String queuemainTopic = splitTopics[2];
//                String queuesubTopic = splitTopics[3];
//                String TopicType = splitTopics.length > 4 ? splitTopics[4] : "";	//서울대전용 프로토콜
//                // Task, Status 구분
//                if (MqttCache.getMqttVehicle(vehicleId,ConnectionState.ConnectionState) == null)
//                {
//                    //				logger.info(String.format("This Vehicle => %s Not Connection State!! ", vehicleId));
//                    continue;
//                }
//
//                if(CommonUtils.isNullOrEmpty(eventInfo.getRequestId()))
//                    continue;
//
//
////        			logger.info(String.format("[%s][Mqtt Subscribe Topics => %s , Vehicle => %s , Message => %s]", Thread.currentThread().getName(),queuetopics,vehicleId,jsonmsg2));
//                // UI로 AMR 정보를 올려주기 위함. TASK
//
//                queuemsg.put(MQTT.TOPIC.Topic, queuesubTopic);
//                if(queuemainTopic.equals(BaseConstants.AMR.Task.Task))
//                {
//                    switch (queuesubTopic)
//                    {
//
//                        case BaseConstants.AMR.Task.FEEDBACK:
//                            TryClass(eventInfo, AMR.Service.FeedbackWork, queuemsg, vehicleId);
//                            break;
//
//                        case BaseConstants.AMR.Task.MOVEREQUEST:
//                            TryClass(eventInfo, BaseConstants.AMR.Service.MoveRequestWork, queuemsg, vehicleId);
//                            break;
//
//                        case BaseConstants.AMR.Task.STATE:
//                            TryClass(eventInfo, AMR.Service.TaskWork, queuemsg, vehicleId);
//                            LogHandler.exec(eventInfo, queuemsg, BaseConstants.EVENTLOG.LOG_TYPE.AMR_Message);
//                            break;
//
//                        default:
//                            break;
//                    }
//                }
//                //Status
//                else if(queuemainTopic.equals(BaseConstants.AMR.Status.STATE))
//                {
//                    if( !queuesubTopic.equals(BaseConstants.AMR.Status.RobotPose))
//                    {
//                        LogHandler.exec(eventInfo, queuemsg, BaseConstants.EVENTLOG.LOG_TYPE.AMR_Message);
//                        logger.info(String.format("[%s][Mqtt Subscribe Topics => %s , Vehicle => %s , Message => %s]", Thread.currentThread().getName(),queuetopics,vehicleId,jsonmsg2));
//                    }
//
//                    // Status 상태정보 기록
//                    switch (queuesubTopic) {
//                        case BaseConstants.AMR.Status.Alarm:
//                            queuemsg.put(BaseConstants.MQTT.TOPIC.Topic, String.join("/", Arrays.copyOfRange(splitTopics, 3, splitTopics.length)));
//                            TryClass(eventInfo, AMR.Service.AlarmWork, queuemsg, vehicleId);
//                            break;
//
//                        case BaseConstants.AMR.Status.BMS:
//                            TryClass(eventInfo, AMR.Service.ChargeWork,queuemsg,vehicleId);
//                            break;
//
//                        case BaseConstants.AMR.Status.RobotPose:
//                            TryClass(eventInfo, AMR.Service.PositionWork, queuemsg, vehicleId);
//                            break;
//
//                        case BaseConstants.AMR.Status.Device:
//                            TryClass(eventInfo, AMR.Service.DeviceWork, queuemsg, vehicleId);
//                            break;
//
//                        case BaseConstants.AMR.Status.navi:
//                            TryClass(eventInfo, AMR.Service.LocationWork, queuemsg, vehicleId);
//                            break;
//
//                        case BaseConstants.AMR.Status.navi_state:
//                            break;
//
//                        case BaseConstants.AMR.Status.EVENT:
//                            TryClass(eventInfo, AMR.Service.ModeWork, queuemsg, vehicleId);
//                            break;
//
//                        default:
//                            break;
//
//                    }
//                }
//                else if(queuemainTopic.equals(BaseConstants.AMR.Management.Management))
//                {
//                    String amrId = JsonUtils.getMessageValue(queuemsg, BaseConstants.AMR.Management.AmrId);
//                    switch(queuesubTopic)
//                    {
//                        case BaseConstants.AMR.Management.AMR_Feedback:
//                            String taskname = JsonUtils.getMessageValue(queuemsg, BaseConstants.AMR.Task.TaskName);
//                            String datatype = JsonUtils.getMessageValue(queuemsg, BaseConstants.AMR.Management.DataType);
//                            Boolean result = Boolean.valueOf(JsonUtils.getMessageValue(queuemsg, BaseConstants.AMR.Task.RESULT));
//
//                            break;
//                        default :
//                            TryClass(eventInfo, AMR.Service.ManagementWork, queuemsg, vehicleId);
//
//                            break;
//
//                    }
//                }
//                else if(queuemainTopic.equals(AMR.Task.CheckCart))
//                {
//                    switch (queuesubTopic) {
//                        case BaseConstants.AMR.Check_Cart.Cart_Request:
//                        {
//                            TryClass(eventInfo, "check_cart_running", queuemsg, vehicleId);
//                            break;
//                        }
//
//                        default:
//                            break;
//                    }
//                }
//                else if(queuemainTopic.equals(AMR.Task.Line))
//                {
//                    switch (queuesubTopic) {
//                        case BaseConstants.AMR.line_order.order_request:
//                        {
//                            TryClass(eventInfo, "line_order_running", queuemsg, vehicleId);
//                            break;
//                        }
//
//                        default:
//                            break;
//                    }
//                }
//                Thread.sleep(5);
//            }
//        }catch (Exception e) { e.printStackTrace(); vehicleThreadList.remove(vehicleId);}
//        vehicleThreadList.remove(vehicleId);
//    };
//}
