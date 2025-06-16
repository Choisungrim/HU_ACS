package com.hubis.acs.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Table(name = "acs_zone_hist")
@Getter
@Setter
@ToString
public class ZoneHist extends BaseEntity {

    @Id
    private Long hist_id; // 일련 번호

    private String zone_id; // 구역의 고유 ID (PK 역할)

    private String zone_nm; // 구역 이름 (UI 표시용 또는 의미 명시)

    private String zone_tp; // 구역 타입 (예: BLOCK, SLOW, WAIT, CUSTOM) 등으로 구분 가능

    private String point_cnt; // point_val 내 좌표 개수

    @Lob
    private String point_val; // [{x:100,y:200}, {x:200,y:200}, ...] 형식의 JSON 문자열로 저장

    private Long map_uuid; // 해당 Zone이 속한 맵 UUID

    private String site_cd; // 사이트 식별 코드

    public ZoneHist() {
    }

    public ZoneHist(Long hist_id) {
        this.hist_id = hist_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZoneHist that = (ZoneHist) o;
        return Objects.equals(hist_id, that.hist_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hist_id);
    }
}
