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
    private String robot_id; // 

    private String robot_tp; // 

    private String model_nm; // 

    private String status_tx; // 

    private double battery_no; // 로봇의 배터리 정보

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)") 
    private Boolean usable_fl; // 데이터 사용 가능 여부

    @Id
    private String site_cd; // SITE 정보

    private String description_tx; // 

    private String prev_activity_tx; // 

    private String activity_tx; // 

    private String creator_by; // 

    private java.time.LocalDateTime create_at; // 생성 시간

    private String modifier_by; // 

    private java.time.LocalDateTime modify_at; // 수정 시간

    private String trans_tx; // 

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
