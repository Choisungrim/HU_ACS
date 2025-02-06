package com.hubis.acs.common.entity.vo;

import java.io.Serializable;
import java.util.Objects;

public class MapMasterId implements Serializable {

    private Long map_uuid;

    private String site_cd;

    public MapMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapMasterId that = (MapMasterId) o;
        return Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map_uuid, site_cd);
    }
}
