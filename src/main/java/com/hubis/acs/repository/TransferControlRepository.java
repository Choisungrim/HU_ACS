package com.hubis.acs.repository;

import com.hubis.acs.common.entity.TransferControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TransferControlRepository extends JpaRepository<TransferControl,Long> {
    @Query("SELECT t " +
            "FROM TransferControl t " +
            "WHERE 1=1 " +
            "AND (t.transfer_status_tx = 'READY' OR t.transfer_status_tx='QUEUED') " +
            "AND t.site_cd = :siteCd " )
    List<TransferControl> findReadyTransfers(@Param("siteCd") String siteCd);

    @Query("SELECT DISTINCT t.assigned_robot_id FROM TransferControl t WHERE t.transfer_status_tx = 'RUNNING'")
    Set<String> findAllAssignedRobotIds();

}
