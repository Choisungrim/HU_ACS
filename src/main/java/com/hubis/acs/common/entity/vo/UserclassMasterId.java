package com.hubis.acs.common.entity.vo;

import java.io.Serializable;
import java.util.Objects;

public class UserclassMasterId implements Serializable {

    private String class_cd;

    private String site_cd;

    public UserclassMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserclassMasterId that = (UserclassMasterId) o;
        return Objects.equals(class_cd, that.class_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(class_cd, site_cd);
    }
}
