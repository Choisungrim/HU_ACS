package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class LangMasterId implements Serializable {

    private String lang_cd;

    private String lang_tp;

    private String site_cd;

    public LangMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LangMasterId that = (LangMasterId) o;
        return Objects.equals(lang_cd, that.lang_cd) && Objects.equals(lang_tp, that.lang_tp) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lang_cd, lang_tp, site_cd);
    }
}
