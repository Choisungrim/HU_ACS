package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_robot_hist")
@Getter
@Setter
@ToString
public class RobotHist extends BaseEntity {

    @Id
    private Long hist_id; // 일련 번호

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

    public RobotHist() {
    }

    public RobotHist(Long hist_id) {
        this.hist_id = hist_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotHist that = (RobotHist) o;
        return Objects.equals(hist_id, that.hist_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hist_id);
    }
}
