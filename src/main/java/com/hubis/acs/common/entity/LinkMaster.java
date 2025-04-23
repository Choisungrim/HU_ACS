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
public class LinkMaster {

    @Id
    private String link_id; // 링크 ID

    private String link_nm; // 링크 명칭

    private String degree_val; // 연결 방향

    @Id
    private Long map_uuid; // 맵 고유 ID

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)") 
    private Boolean usable_fl; // 데이터 사용 가능 여부

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
