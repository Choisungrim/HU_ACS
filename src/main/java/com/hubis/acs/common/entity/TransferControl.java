package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.TransferControlId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(TransferControlId.class)
@Entity
@Table(name = "acs_transfer_control")
@Getter
@Setter
@ToString
public class TransferControl {

    @Id
    private String transfer_id; // 작업 ID

    private String transfer_tp; // 작업 타입

    private String assigned_robot_id; // 작업 할당 로봇 ID

    private String transfer_st; // 작업 상태

    private int priority_no; // 작업 우선 순위

    private String source_port_id; // 작업 대상 포트 명칭

    private String destination_port_id; // 최종 목적지 예치 포트 명칭

    private java.time.LocalDateTime acquire_start_at; // 물품 수령 시작 시간

    private java.time.LocalDateTime acquire_end_at; // 물품 수령 완료 시간

    private java.time.LocalDateTime deposit_start_at; // 물품 예치 시작 시간

    private java.time.LocalDateTime deposit_end_at; // 물품 예치 완료 시간

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)") 
    private Boolean usable_fl; // 데이터 사용 가능 여부

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

    public TransferControl() {
    }

    public TransferControl(String transfer_id, String site_cd) {
        this.transfer_id = transfer_id;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferControl that = (TransferControl) o;
        return Objects.equals(transfer_id, that.transfer_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transfer_id, site_cd);
    }
}
