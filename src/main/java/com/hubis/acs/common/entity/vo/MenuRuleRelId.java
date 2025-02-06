package com.hubis.acs.common.entity.vo;

import java.io.Serializable;
import java.util.Objects;

public class MenuRuleRelId implements Serializable {

    private String menu_cd;

    private String rule_cd;

    private String site_cd;

    public MenuRuleRelId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuRuleRelId that = (MenuRuleRelId) o;
        return Objects.equals(menu_cd, that.menu_cd) && Objects.equals(rule_cd, that.rule_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menu_cd, rule_cd, site_cd);
    }
}
