package com.hubis.acs.common.entity;

import com.hubis.acs.common.entity.vo.AreaMasterId;
import com.hubis.acs.common.entity.vo.ZoneMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@IdClass(ZoneMasterId.class)
@Entity
@Table(name = "acs_zone_master")
@Getter
@Setter
@ToString
public class ZoneMaster extends BaseEntity {

    @Id
    private String zone_id; // 영역 ID

    private String zone_nm; // 영역 명칭

    private String zone_tp; // 영역 타입

    private String point_cnt; // 영역 포인트 개수

    @Lob
    private String point_val; // 포인트 정보

    @Id
    private Long map_uuid; // 맵 고유 ID

    @Id
    private String site_cd; // SITE 정보

    public ZoneMaster() {
    }

    public ZoneMaster(String zone_id, Long map_uuid, String site_cd) {
        this.zone_id = zone_id;
        this.map_uuid = map_uuid;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZoneMaster that = (ZoneMaster) o;
        return Objects.equals(zone_id, that.zone_id) && Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zone_id, map_uuid, site_cd);
    }
}
