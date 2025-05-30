package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class RoleMasterId implements Serializable {

    private String role_cd;

    private String site_cd;

    public RoleMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleMasterId that = (RoleMasterId) o;
        return Objects.equals(role_cd, that.role_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role_cd, site_cd);
    }
}
