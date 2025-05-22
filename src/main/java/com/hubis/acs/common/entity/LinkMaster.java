package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.LinkMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(LinkMasterId.class)
@Entity
@Table(name = "acs_link_master")
@Getter
@Setter
@ToString
public class LinkMaster extends BaseEntity {

    @Id
    private String link_id; // 링크 ID

    private String link_nm; // 링크 명칭

    private String degree_val; // 연결 방향

    @Id
    private Long map_uuid; // 맵 고유 ID

    @Id
    private String site_cd; // SITE 정보

    public LinkMaster() {
    }

    public LinkMaster(String link_id, Long map_uuid, String site_cd) {
        this.link_id = link_id;
        this.map_uuid = map_uuid;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkMaster that = (LinkMaster) o;
        return Objects.equals(link_id, that.link_id) && Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link_id, map_uuid, site_cd);
    }
}
