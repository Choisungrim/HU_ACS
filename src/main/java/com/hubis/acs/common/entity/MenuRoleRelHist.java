package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_menu_role_rel_hist")
@Getter
@Setter
@ToString
public class MenuRoleRelHist extends BaseEntity {

    @Id
    private Long hist_id; // 일련 번호

    private String menu_cd; // 메뉴 고유 코드

    private String role_cd; // 규칙 코드

    private String site_cd; // SITE 정보

    public MenuRoleRelHist() {
    }

    public MenuRoleRelHist(String menu_cd, String role_cd, String site_cd) {
        this.menu_cd = menu_cd;
        this.role_cd = role_cd;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuRoleRelHist that = (MenuRoleRelHist) o;
        return Objects.equals(menu_cd, that.menu_cd) && Objects.equals(role_cd, that.role_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menu_cd, role_cd, site_cd);
    }
}
