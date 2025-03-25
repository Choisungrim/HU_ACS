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
    private String transfer_id; // 

    private String transfer_tp; // 

    private String assigned_robot_id; // 

    private String transfer_st; // 

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

    private String description_tx; // 

    private String prev_activity_tx; // 

    private String activity_tx; // 

    private String creator_by; // 

    private java.time.LocalDateTime create_at; // 생성 시간

    private String modifier_by; // 

    private java.time.LocalDateTime modify_at; // 수정 시간

    private String trans_tx; // 

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
