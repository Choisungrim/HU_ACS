package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class AreaMasterId implements Serializable {

    private String area_id;

    private Long map_uuid;

    private String site_cd;

    public AreaMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AreaMasterId that = (AreaMasterId) o;
        return Objects.equals(area_id, that.area_id) && Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(area_id, map_uuid, site_cd);
    }
}
