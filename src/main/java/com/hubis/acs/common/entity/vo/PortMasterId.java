package com.hubis.acs.common.entity.vo;

import java.io.Serializable;
import java.util.Objects;

public class PortMasterId implements Serializable {

    private String port_id;

    private String equipment_id;

    private String site_cd;

    public PortMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortMasterId that = (PortMasterId) o;
        return Objects.equals(port_id, that.port_id) && Objects.equals(equipment_id, that.equipment_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port_id, equipment_id, site_cd);
    }
}
