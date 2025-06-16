package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.RobotMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(RobotMasterId.class)
@Entity
@Table(name = "acs_robot_master")
@Getter
@Setter
@ToString
public class RobotMaster extends BaseEntity {

    @Id
    private String robot_id; // 로봇 ID

    private String robot_tp; // 로봇 타입

    private String model_nm; // 모델명

    private String status_tx; // 상태

    private String transfer_id; // 작업 ID

    private String location_nm; // 로봇의 현재 위치 정보

    private String wait_location_nm; // 로봇의 고정 대기위치 | 빈 경우 동적 대기

    @Column(name = "detection_fl", columnDefinition = "TINYINT(1)")
    private Integer detection_fl; // 로봇의 충전 위치 정보

    private double battery_no; // 로봇의 배터리 정보

    private String charge_rule_id; // 충전 규칙정보

    private Long map_uuid; // 맵 고유 ID

    @Id
    private String site_cd; // SITE 정보

    public RobotMaster() {
    }

    public RobotMaster(String robot_id, String site_cd) {
        this.robot_id = robot_id;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotMaster that = (RobotMaster) o;
        return Objects.equals(robot_id, that.robot_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(robot_id, site_cd);
    }
}
