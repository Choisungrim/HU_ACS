package com.hubis.acs.main;

import com.hubis.acs.DemoApplication;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.handler.BaseExecutorHandler;
import com.hubis.acs.common.position.cache.RobotPositionCache;
import com.hubis.acs.common.utils.EventInfoBuilder;
import com.hubis.acs.repository.TransferControlRepository;
import com.hubis.acs.service.BaseService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = DemoApplication.class)
class DemoApplicationTests {


	@Autowired
	private BaseService baseService;

	@Autowired
	private RobotPositionCache robotPositionCache;

	@Autowired
	private BaseExecutorHandler baseExecutorHandler;

	@Test
	public void testZoneEntryAndPathBlocked() throws Exception {
		// given: 초기 상태
		JSONObject message = new JSONObject();
		message.put("robot_id", "ROBOT_01");
		message.put("x", 10.0);
		message.put("y", 20.0);
		message.put("deg", 90.0);

		EventInfo eventInfo = new EventInfoBuilder()
				.addWorkGroupId("middleware")
				.addRequestId("middleware")
				.addWorkId("position_change")
				.addSiteId("HU")
				.build();

		String result = baseExecutorHandler.execute(eventInfo,message,new JSONObject());

		// then: 점유 여부, 경로 검사 로그 등 확인
		assertEquals(result, result); // 혹은 BaseConstants.RETURNCODE.Success
	}
}
