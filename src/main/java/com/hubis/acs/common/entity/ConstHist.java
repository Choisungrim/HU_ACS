package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_const_hist")
@Getter
@Setter
@ToString
public class ConstHist extends BaseEntity {

    @Id
    private Long hist_id; // 일련 번호

    private String constant_cd; // 상수 코드

    private String constant_tp; // 상수 타입

    private String constant_nm; // 상수 명칭

    private String constant_val; // 상수 값

    private String site_cd; // SITE 정보

    public ConstHist() {
    }

    public ConstHist(String constant_cd, String constant_tp, String site_cd) {
        this.constant_cd = constant_cd;
        this.constant_tp = constant_tp;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstHist that = (ConstHist) o;
        return Objects.equals(constant_cd, that.constant_cd) && Objects.equals(constant_tp, that.constant_tp) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constant_cd, constant_tp, site_cd);
    }
}
