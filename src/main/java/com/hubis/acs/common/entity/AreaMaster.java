package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.AreaMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(AreaMasterId.class)
@Entity
@Table(name = "acs_area_master")
@Getter
@Setter
@ToString
public class AreaMaster {

    @Id
    private String area_id; // 영역 ID

    private String area_nm; // 

    private String area_tp; // 

    private String point_cnt; // 

    @Lob
    private String point_val; // 포인트 정보

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

    public AreaMaster() {
    }

    public AreaMaster(String area_id, Long map_uuid, String site_cd) {
        this.area_id = area_id;
        this.map_uuid = map_uuid;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AreaMaster that = (AreaMaster) o;
        return Objects.equals(area_id, that.area_id) && Objects.equals(map_uuid, that.map_uuid) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(area_id, map_uuid, site_cd);
    }
}
