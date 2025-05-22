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
public class LangMaster extends BaseEntity {

    @Id
    private String lang_cd; // 다국어 코드

    @Id
    private String lang_tp; // 다국어 타입

    private String lang_val; // 다국어 값

    @Id
    private String site_cd; // SITE 정보

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
