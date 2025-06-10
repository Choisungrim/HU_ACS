package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class ZoneMasterId implements Serializable {

    private String zone_id;

    private Long map_uuid;

    private String site_cd;

    public ZoneMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZoneMasterId that = (ZoneMasterId) o;
        return Objects.equals(zone_id, that.zone_id) && Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zone_id, map_uuid, site_cd);
    }
}
