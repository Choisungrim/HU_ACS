package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.PortMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(PortMasterId.class)
@Entity
@Table(name = "acs_port_master")
@Getter
@Setter
@ToString
public class PortMaster extends BaseEntity {

    @Id
    private String port_id; // 포트 명칭

    private String port_tp; // 포트 타입

    private String status_tx; // 상태

    @Id
    private String equipment_id; // 포트의 설비

    private String node_id; // 포트의 위치정보

    @Id
    private String site_cd; // SITE 정보

    public PortMaster() {
    }

    public PortMaster(String port_id, String equipment_id, String site_cd) {
        this.port_id = port_id;
        this.equipment_id = equipment_id;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortMaster that = (PortMaster) o;
        return Objects.equals(port_id, that.port_id) && Objects.equals(equipment_id, that.equipment_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port_id, equipment_id, site_cd);
    }
}
