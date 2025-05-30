package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class CarrierMasterId implements Serializable {

    private String carrier_id;

    private String port_id;

    private String site_cd;

    public CarrierMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrierMasterId that = (CarrierMasterId) o;
        return Objects.equals(carrier_id, that.carrier_id) && Objects.equals(port_id, that.port_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carrier_id, port_id, site_cd);
    }
}
