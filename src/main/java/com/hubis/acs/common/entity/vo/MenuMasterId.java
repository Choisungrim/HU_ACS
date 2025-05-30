package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class MenuMasterId implements Serializable {

    private String menu_cd;

    private String site_cd;

    public MenuMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuMasterId that = (MenuMasterId) o;
        return Objects.equals(menu_cd, that.menu_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menu_cd, site_cd);
    }
}
