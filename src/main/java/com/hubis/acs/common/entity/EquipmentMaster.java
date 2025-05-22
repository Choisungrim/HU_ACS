package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.EquipmentMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(EquipmentMasterId.class)
@Entity
@Table(name = "acs_equipment_master")
@Getter
@Setter
@ToString
public class EquipmentMaster extends BaseEntity {

    @Id
    private String equipment_id; // 설비 명칭

    private String equipment_tp; // 설비 타입

    private String status_tx; // 상태

    @Id
    private String site_cd; // SITE 정보

    public EquipmentMaster() {
    }

    public EquipmentMaster(String equipment_id, String site_cd) {
        this.equipment_id = equipment_id;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentMaster that = (EquipmentMaster) o;
        return Objects.equals(equipment_id, that.equipment_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipment_id, site_cd);
    }
}
