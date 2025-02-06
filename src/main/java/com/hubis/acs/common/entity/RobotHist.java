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
public class RobotHist {

    @Id
    private Long hist_id; // 일련 번호

    private String robot_id; // 로봇의 명칭

    private String robot_tp; // 로봇의 타입

    private String model_nm; // 로봇의 모델

    private String status_tx; // 로봇의 상태

    private double battery_no; // 로봇의 배터리 정보

    private boolean usable_fl; // 데이터 사용 가능 여부

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
