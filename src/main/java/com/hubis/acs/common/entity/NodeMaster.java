package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.NodeMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(NodeMasterId.class)
@Entity
@Table(name = "acs_node_master")
@Getter
@Setter
@ToString
public class NodeMaster extends BaseEntity {

    @Id
    private String node_id; // 노드 ID

    private String node_nm; // 노드 명칭

    private String pos_x_val; // X 좌표

    private String pos_y_val; // Y 좌표

    private String degree_val; // 정위치 회전 각도

    private String occpyied_robot_id; // 점유 로봇 ID

    private String area_id; // 영역 정보

    @Id
    private Long map_uuid; // 맵 고유 ID

    @Id
    private String site_cd; // SITE 정보

    public NodeMaster() {
    }

    public NodeMaster(String node_id, Long map_uuid, String site_cd) {
        this.node_id = node_id;
        this.map_uuid = map_uuid;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeMaster that = (NodeMaster) o;
        return Objects.equals(node_id, that.node_id) && Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node_id, map_uuid, site_cd);
    }
}
