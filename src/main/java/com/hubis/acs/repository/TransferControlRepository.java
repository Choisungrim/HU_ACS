package com.hubis.acs.repository;

import com.hubis.acs.common.entity.TransferControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TransferControlRepository extends JpaRepository<TransferControl,Long> {
//    List<TransferControl> findTransferControlByTransferId(Long transferId);
    @Query("SELECT t " +
            "FROM TransferControl t " +
            "WHERE 1=1 " +
            "AND (t.transfer_st = 'READY' OR t.transfer_st='QUEUED') " +
            "AND t.site_cd = :siteCd " )
    List<TransferControl> findReadyTransfers(String siteCd);

    @Query("SELECT DISTINCT t.assigned_robot_id FROM TransferControl t WHERE t.transfer_st = 'RUNNING'")
    Set<String> findAllAssignedRobotIds();

}
