package com.hubis.acs.common.entity.vo;

import java.io.Serializable;
import java.util.Objects;

public class RuleMasterId implements Serializable {

    private String rule_cd;

    private String site_cd;

    public RuleMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleMasterId that = (RuleMasterId) o;
        return Objects.equals(rule_cd, that.rule_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule_cd, site_cd);
    }
}
