package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_micro_transfer_control_hist")
@Getter
@Setter
@ToString
public class MicroTransferControlHist {

    @Id
    private Long hist_id; // 일련 번호

    private String micro_transfer_id; // 세부작업 ID

    private String transfer_id; // 그룹 작업 ID

    private String micro_transfer_tp; // 세부작업 타입

    private String assigned_robot_id; // 작업 할당 로봇 ID

    private String micro_transfer_st; // 세부작업 상태

    private int priority_no; // 세부작업 우선 순위

    private String from_tx; // 세부작업의 시작지위치

    private String to_tx; // 세부작업의 목적지위치

    private java.time.LocalDateTime micro_transfer_start_at; // 세부작업의 시작 시간

    private java.time.LocalDateTime micro_transfer_end_at; // 세부작업의 완료 시간

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)") 
    private Boolean usable_fl; // 데이터 사용 가능 여부

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

    public MicroTransferControlHist() {
    }

    public MicroTransferControlHist(Long hist_id) {
        this.hist_id = hist_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MicroTransferControlHist that = (MicroTransferControlHist) o;
        return Objects.equals(hist_id, that.hist_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hist_id);
    }
}
