package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.MenuRoleRelId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(MenuRoleRelId.class)
@Entity
@Table(name = "acs_menu_role_rel")
@Getter
@Setter
@ToString
public class MenuRoleRel extends BaseEntity {

    @Id
    private String menu_cd; // 메뉴 고유 코드

    @Id
    private String role_cd; // 규칙 코드

    @Id
    private String site_cd; // SITE 정보

    public MenuRoleRel() {
    }

    public MenuRoleRel(String menu_cd, String role_cd, String site_cd) {
        this.menu_cd = menu_cd;
        this.role_cd = role_cd;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuRoleRel that = (MenuRoleRel) o;
        return Objects.equals(menu_cd, that.menu_cd) && Objects.equals(role_cd, that.role_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menu_cd, role_cd, site_cd);
    }
}
