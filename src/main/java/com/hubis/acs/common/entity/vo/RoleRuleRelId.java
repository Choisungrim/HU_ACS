package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class RoleRuleRelId implements Serializable {

    private String role_cd;

    private String rule_cd;

    private String site_cd;

    public RoleRuleRelId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleRuleRelId that = (RoleRuleRelId) o;
        return Objects.equals(role_cd, that.role_cd) && Objects.equals(rule_cd, that.rule_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role_cd, rule_cd, site_cd);
    }
}
