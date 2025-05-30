package com.hubis.acs.main;

import com.hubis.acs.DemoApplication;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.repository.TransferControlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = DemoApplication.class)
class DemoApplicationTests {


	@Autowired
	private TransferControlRepository repository;

	@Test
	public void testFindPendingTransfers() {
		List<TransferControl> result = repository.findReadyTransfers("HU");
		assertNotNull(result);
		result.forEach(transfer -> {
			System.out.println("Transfer: " + transfer.getTransfer_id()+ ", 상태: " + transfer.getTransfer_st());
		});
	}


}
