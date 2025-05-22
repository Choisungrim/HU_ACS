package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.CarrierMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(CarrierMasterId.class)
@Entity
@Table(name = "acs_carrier_master")
@Getter
@Setter
@ToString
public class CarrierMaster extends BaseEntity {

    @Id
    private String carrier_id; // carrier ID

    private String carrier_tp; // carrier 타입

    private String status_tx; // 상태

    @Id
    private String port_id; // 포트 명칭

    @Id
    private String site_cd; // SITE 정보

    public CarrierMaster() {
    }

    public CarrierMaster(String carrier_id, String port_id, String site_cd) {
        this.carrier_id = carrier_id;
        this.port_id = port_id;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrierMaster that = (CarrierMaster) o;
        return Objects.equals(carrier_id, that.carrier_id) && Objects.equals(port_id, that.port_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carrier_id, port_id, site_cd);
    }
}
