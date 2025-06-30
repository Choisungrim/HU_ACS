package com.hubis.acs.common.entity;

import com.hubis.acs.common.entity.vo.AlarmMasterId;
import com.hubis.acs.common.entity.vo.ModelMasterId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@IdClass(ModelMasterId.class)
@Entity
@Table(name = "acs_model_master")
@Getter
@Setter
@ToString
public class ModelMaster extends BaseEntity {

    @Id
    private String model_nm; // 영역 ID

    @Id
    private String vendor_nm; // 영역 명칭

    private String protocol_tp; // 영역 타입

    @Id
    private String site_cd; // SITE 정보

    public ModelMaster() {
    }

    public ModelMaster(String model_nm, String vendor_nm, String site_cd) {
        this.model_nm = model_nm;
        this.vendor_nm = vendor_nm;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelMaster that = (ModelMaster) o;
        return Objects.equals(model_nm, that.model_nm) && Objects.equals(vendor_nm, that.vendor_nm) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model_nm, vendor_nm, site_cd);
    }
}
