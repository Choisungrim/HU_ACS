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
public class MapMaster {

    @Id
    private Long map_uuid; // 맵 고유 ID

    private String map_nm; // 맵 이름

    @Lob
    private String map_val; // 맵 정보

    private boolean map_res; // 맵 해상도

    private String map_ver; // 버전

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
