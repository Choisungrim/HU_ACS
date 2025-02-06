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
public class RobotMaster {

    @Id
    private String robot_id; // 로봇의 명칭

    private String robot_tp; // 로봇의 타입

    private String model_nm; // 로봇의 모델

    private String status_tx; // 로봇의 상태

    private double battery_no; // 로봇의 배터리 정보

    private boolean usable_fl; // 데이터 사용 가능 여부

    @Id
    private String site_cd; // SITE 정보

    private String description_tx; // 데이터에 대한 설명

    private String prev_activity_tx; // 이전 활동 내용

    private String activity_tx; // 현재 활동 내용

    private String creator_by; // 데이터 생성자

    private java.time.LocalDateTime create_at; // 생성 시간

    private String modifier_by; // 데이터 수정자

    private java.time.LocalDateTime modify_at; // 수정 시간

    private String trans_tx; // 관련 트랜잭션 ID

    private java.time.LocalDateTime last_event_at; // 최근 이벤트 발생 시간

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
