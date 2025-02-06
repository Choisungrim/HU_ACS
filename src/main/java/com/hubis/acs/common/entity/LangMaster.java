package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.LangMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(LangMasterId.class)
@Entity
@Table(name = "acs_lang_master")
@Getter
@Setter
@ToString
public class LangMaster {

    @Id
    private String lang_cd; // 다국어 코드

    @Id
    private String lang_tp; // 다국어 타입

    private String lang_val; // 다국어 값

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

    public LangMaster() {
    }

    public LangMaster(String lang_cd, String lang_tp, String site_cd) {
        this.lang_cd = lang_cd;
        this.lang_tp = lang_tp;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LangMaster that = (LangMaster) o;
        return Objects.equals(lang_cd, that.lang_cd) && Objects.equals(lang_tp, that.lang_tp) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lang_cd, lang_tp, site_cd);
    }
}
