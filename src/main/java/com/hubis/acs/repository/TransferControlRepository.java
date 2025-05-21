package com.hubis.acs.repository;

import com.hubis.acs.common.entity.TransferControl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferControlRepository extends JpaRepository<TransferControl,Long> {
//    List<TransferControl> findTransferControlByTransferId(Long transferId);
}
