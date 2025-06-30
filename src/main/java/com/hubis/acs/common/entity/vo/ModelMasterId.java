package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class ModelMasterId implements Serializable {

    private String model_nm ;

    private String vendor_nm;

    private String site_cd;

    public ModelMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelMasterId that = (ModelMasterId) o;
        return Objects.equals(model_nm, that.model_nm) && Objects.equals(vendor_nm, that.vendor_nm) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model_nm, vendor_nm, site_cd);
    }
}
