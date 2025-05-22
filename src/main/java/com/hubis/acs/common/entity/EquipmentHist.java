package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_equipment_hist")
@Getter
@Setter
@ToString
public class EquipmentHist extends BaseEntity {

    @Id
    private Long hist_id; // 일련 번호

    private String equipment_id; // 설비 명칭

    private String equipment_tp; // 설비 타입

    private String status_tx; // 상태

    private String site_cd; // SITE 정보

    public EquipmentHist() {
    }

    public EquipmentHist(Long hist_id) {
        this.hist_id = hist_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentHist that = (EquipmentHist) o;
        return Objects.equals(hist_id, that.hist_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hist_id);
    }
}
