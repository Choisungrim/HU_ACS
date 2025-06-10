package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class NodeMasterId implements Serializable {

    private String node_id;

    private Long map_uuid;

    private String site_cd;

    public NodeMasterId() {}
    public NodeMasterId(String node_id, Long map_uuid, String site_cd) { this.node_id = node_id; this.map_uuid = map_uuid; this.site_cd = site_cd; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeMasterId that = (NodeMasterId) o;
        return Objects.equals(node_id, that.node_id) && Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node_id, map_uuid, site_cd);
    }
}
