package com.hubis.acs.middleware.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.vo.RobotMasterId;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.CommonUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_battery_change")
public class BatteryChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(BatteryChange.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();
        double soc = message.optDouble("soc");

        RobotMaster robot = baseService.findById(RobotMaster.class, new RobotMasterId(robotId,siteId));
        if(CommonUtils.isNullOrEmpty(robot))
        {
            robot.setBattery_no(soc);
            baseService.saveOrUpdate(eventInfo, robot);
        }
        else
            return BaseConstants.RETURNCODE.Fail;

        return result;
    }
}
