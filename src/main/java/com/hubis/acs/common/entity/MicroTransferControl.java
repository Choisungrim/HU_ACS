package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.MicroTransferControlId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(MicroTransferControlId.class)
@Entity
@Table(name = "acs_micro_transfer_control")
@Getter
@Setter
@ToString
public class MicroTransferControl extends BaseEntity{

    @Id
    private String micro_transfer_id; // 세부작업 ID

    @Id
    private String transfer_id; // 그룹 작업 ID

    private String micro_transfer_tp; // 세부작업 타입

    private String assigned_robot_id; // 작업 할당 로봇 ID

    private String micro_transfer_st; // 세부작업 상태

    private int priority_no; // 세부작업 우선 순위

    private String from_tx; // 세부작업의 시작지위치

    private String to_tx; // 세부작업의 목적지위치

    private java.time.LocalDateTime micro_transfer_start_at; // 세부작업의 시작 시간

    private java.time.LocalDateTime micro_transfer_end_at; // 세부작업의 완료 시간

    @Id
    private String site_cd; // SITE 정보

    public MicroTransferControl() {
    }

    public MicroTransferControl(String micro_transfer_id, String transfer_id, String site_cd) {
        this.micro_transfer_id = micro_transfer_id;
        this.transfer_id = transfer_id;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MicroTransferControl that = (MicroTransferControl) o;
        return Objects.equals(micro_transfer_id, that.micro_transfer_id) && Objects.equals(transfer_id, that.transfer_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(micro_transfer_id, transfer_id, site_cd);
    }
}
