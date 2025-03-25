package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_transfer_control_hist")
@Getter
@Setter
@ToString
public class TransferControlHist {

    @Id
    private Long hist_id; // 일련 번호

    private String transfer_id; // 

    private String transfer_tp; // 

    private String assigned_robot_id; // 

    private String transfer_st; // 

    private String source_port_id; // 

    private String destination_port_id; // 

    private java.time.LocalDateTime acquire_start_at; // 물품 수령 시작 시간

    private java.time.LocalDateTime acquire_end_at; // 물품 수령 완료 시간

    private java.time.LocalDateTime deposit_start_at; // 물품 예치 시작 시간

    private java.time.LocalDateTime deposit_end_at; // 물품 예치 완료 시간

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)") 
    private Boolean usable_fl; // 데이터 사용 가능 여부

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

    public TransferControlHist() {
    }

    public TransferControlHist(Long hist_id) {
        this.hist_id = hist_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferControlHist that = (TransferControlHist) o;
        return Objects.equals(hist_id, that.hist_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hist_id);
    }
}
