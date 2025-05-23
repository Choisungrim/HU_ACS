package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.RoleRuleRelId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(RoleRuleRelId.class)
@Entity
@Table(name = "acs_role_rule_rel")
@Getter
@Setter
@ToString
public class RoleRuleRel extends BaseEntity{

    @Id
    private String role_cd; // 역할 코드

    @Id
    private String rule_cd; // 규칙 코드

    @Id
    private String site_cd; // SITE 정보


    public RoleRuleRel() {
    }

    public RoleRuleRel(String role_cd, String rule_cd, String site_cd) {
        this.role_cd = role_cd;
        this.rule_cd = rule_cd;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleRuleRel that = (RoleRuleRel) o;
        return Objects.equals(role_cd, that.role_cd) && Objects.equals(rule_cd, that.rule_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role_cd, rule_cd, site_cd);
    }
}
