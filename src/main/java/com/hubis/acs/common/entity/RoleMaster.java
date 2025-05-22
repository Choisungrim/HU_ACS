package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.RoleMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(RoleMasterId.class)
@Entity
@Table(name = "acs_role_master")
@Getter
@Setter
@ToString
public class RoleMaster extends BaseEntity{

    @Id
    private String role_cd; // 역할 코드

    private String role_nm; // 역할 이름

    @Id
    private String site_cd; // SITE 정보

    public RoleMaster() {
    }

    public RoleMaster(String role_cd, String site_cd) {
        this.role_cd = role_cd;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleMaster that = (RoleMaster) o;
        return Objects.equals(role_cd, that.role_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role_cd, site_cd);
    }
}
