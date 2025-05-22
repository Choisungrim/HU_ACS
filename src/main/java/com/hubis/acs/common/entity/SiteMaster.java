package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_site_master")
@Getter
@Setter
@ToString
public class SiteMaster extends BaseEntity {

    @Id
    private String site_cd; // 사이트 고유 코드

    private String site_nm; // 사이트 명칭

    public SiteMaster() {
    }

    public SiteMaster(String site_cd) {
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteMaster that = (SiteMaster) o;
        return Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(site_cd);
    }
}
