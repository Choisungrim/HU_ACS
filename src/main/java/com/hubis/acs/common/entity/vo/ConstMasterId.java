package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class ConstMasterId implements Serializable {

    private String constant_cd;

    private String constant_tp;

    private String site_cd;

    public ConstMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstMasterId that = (ConstMasterId) o;
        return Objects.equals(constant_cd, that.constant_cd) && Objects.equals(constant_tp, that.constant_tp) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constant_cd, constant_tp, site_cd);
    }
}
