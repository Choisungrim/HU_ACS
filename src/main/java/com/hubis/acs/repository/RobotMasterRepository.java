package com.hubis.acs.repository;

import com.hubis.acs.common.entity.RobotMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RobotMasterRepository extends JpaRepository<RobotMaster,Long> {
    @Query("SELECT r FROM RobotMaster r WHERE r.status_tx = 'idle' AND r.detection_fl = 0 AND r.usable_fl = 1 AND r.site_cd = :siteCd")
    List<RobotMaster> findAvailableRobots(@Param("siteCd")String siteCd);

    @Query("SELECT r FROM RobotMaster r WHERE r.status_tx = 'idle' AND r.usable_fl = 1 AND r.detection_fl = 0 AND r.site_cd = :siteCd AND r.robot_id = :robotId")
    List<RobotMaster> findAvailableRobotById(@Param("robotId")String robotId, @Param("siteCd")String siteCd);

}
