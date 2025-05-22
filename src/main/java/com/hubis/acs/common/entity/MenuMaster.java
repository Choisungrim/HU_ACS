package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.MenuMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(MenuMasterId.class)
@Entity
@Table(name = "acs_menu_master")
@Getter
@Setter
@ToString
public class MenuMaster extends BaseEntity {

    @Id
    private String menu_cd; // 메뉴 고유 코드

    private String menu_nm; // 메뉴 이름

    private String menu_url; // 메뉴 URL

    private String parent_id; // 상위 메뉴 ID

    private int menu_seq; // 메뉴 정렬 순서

    private int menu_depth; // 메뉴 깊이 (0: 최상위, 1: 1단계 하위, ...)

    @Id
    private String site_cd; // SITE 정보

    public MenuMaster() {
    }

    public MenuMaster(String menu_cd, String site_cd) {
        this.menu_cd = menu_cd;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuMaster that = (MenuMaster) o;
        return Objects.equals(menu_cd, that.menu_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menu_cd, site_cd);
    }
}
