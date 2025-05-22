package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.MapMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(MapMasterId.class)
@Entity
@Table(name = "acs_map_master")
@Getter
@Setter
@ToString
public class MapMaster extends BaseEntity {

    @Id
    private Long map_uuid; // 맵 고유 ID

    private String map_nm; // 맵 명칭

    @Lob
    private String map_val; // 맵 정보

    private Boolean map_res; // 맵 해상도

    private String map_ver; // 맵 버전

    @Id
    private String site_cd; // SITE 정보

    public MapMaster() {
    }

    public MapMaster(Long map_uuid, String site_cd) {
        this.map_uuid = map_uuid;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapMaster that = (MapMaster) o;
        return Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map_uuid, site_cd);
    }
}
