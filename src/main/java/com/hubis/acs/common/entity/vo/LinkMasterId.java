package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class LinkMasterId implements Serializable {

    private String link_id;

    private Long map_uuid;

    private String site_cd;

    public LinkMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkMasterId that = (LinkMasterId) o;
        return Objects.equals(link_id, that.link_id) && Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link_id, map_uuid, site_cd);
    }
}
