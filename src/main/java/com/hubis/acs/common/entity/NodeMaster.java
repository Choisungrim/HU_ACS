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
public class NodeMaster {

    @Id
    private String node_id; // 노드 ID

    private String node_nm; // 

    private String pos_x_val; // 

    private String pos_y_val; // 

    private String degree_val; // 

    private String occpyied_robot_id; // 

    private String area_id; // 영역 정보

    @Id
    private Long map_uuid; // 맵 고유 ID

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)") 
    private Boolean usable_fl; // 데이터 사용 가능 여부

    @Id
    private String site_cd; // SITE 정보

    private String description_tx; // 

    private String prev_activity_tx; // 

    private String activity_tx; // 

    private String creator_by; // 

    private java.time.LocalDateTime create_at; // 생성 시간

    private String modifier_by; // 

    private java.time.LocalDateTime modify_at; // 수정 시간

    private String trans_tx; // 

    private java.time.LocalDateTime last_event_at; // 최근 이벤트 발생 시간

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
