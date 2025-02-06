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

    private String node_nm; // 노드 명칭

    private String pos_x_val; // 노드 x좌표

    private String pos_y_val; // 노드 y좌표

    private String degree_val; // 노드 지정 방향값

    private String occpyied_robot_id; // 노드 점유 로봇

    private String area_id; // 영역 정보

    @Id
    private Long map_uuid; // 맵 고유 ID

    private boolean usable_fl; // 데이터 사용 가능 여부

    @Id
    private String site_cd; // SITE 정보

    private String description_tx; // 데이터에 대한 설명

    private String prev_activity_tx; // 이전 활동 내용

    private String activity_tx; // 현재 활동 내용

    private String creator_by; // 데이터 생성자

    private java.time.LocalDateTime create_at; // 생성 시간

    private String modifier_by; // 데이터 수정자

    private java.time.LocalDateTime modify_at; // 수정 시간

    private String trans_tx; // 관련 트랜잭션 ID

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
