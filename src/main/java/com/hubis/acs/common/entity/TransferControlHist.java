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
public class TransferControlHist extends BaseEntity{

    @Id
    private Long hist_id; // 일련 번호

    private String transfer_id; // 작업 ID
    private String transfer_tp; // 작업 타입
    private String assigned_robot_id; // 작업 할당 로봇 ID
    private String transfer_status_tx; // 작업 상태
    private String sub_status_tx;// 작업의 세부 상태
    private int priority_no; // 작업 우선 순위
    private String carrier_id; // 대차정보
    private String source_port_id; // 작업 대상 포트 명칭
    private String destination_port_id; // 최종 목적지 예치 포트 명칭
    private java.time.LocalDateTime load_start_at; // 물품 수령 시작 시간
    private java.time.LocalDateTime load_end_at; // 물품 수령 완료 시간
    private java.time.LocalDateTime unload_start_at; // 물품 예치 시작 시간
    private java.time.LocalDateTime unload_end_at; // 물품 예치 완료 시간
    private java.time.LocalDateTime job_complete_at; // 작업 완료 시간
    private String site_cd; // SITE 정보

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
