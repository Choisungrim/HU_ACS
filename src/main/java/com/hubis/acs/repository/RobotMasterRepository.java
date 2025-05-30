package com.hubis.acs.repository;

import com.hubis.acs.common.entity.RobotMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RobotMasterRepository extends JpaRepository<RobotMaster,Long> {
    @Query("SELECT r FROM RobotMaster r WHERE r.status_tx = 'idle' AND r.usable_fl = 1 AND r.site_cd = :siteCd")
    List<RobotMaster> findAvailableRobots(String siteCd);

    @Query("SELECT r FROM RobotMaster r WHERE r.status_tx = 'idle' AND r.usable_fl = 1 AND r.site_cd = :siteCd AND r.robot_id = :robotId")
    List<RobotMaster> findAvailableRobotById(String robotId, String siteCd);

}
