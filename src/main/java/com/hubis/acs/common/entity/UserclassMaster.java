package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.UserclassMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(UserclassMasterId.class)
@Entity
@Table(name = "acs_userclass_master")
@Getter
@Setter
@ToString
public class UserclassMaster extends BaseEntity{

    @Id
    private String class_cd; // 그룹 코드

    private String class_nm; // 그룹 명칭

    private String role_cd; // 그룹에 할당된 역할 코드

    @Id
    private String site_cd; // SITE 정보

    public UserclassMaster() {
    }

    public UserclassMaster(String class_cd, String site_cd) {
        this.class_cd = class_cd;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserclassMaster that = (UserclassMaster) o;
        return Objects.equals(class_cd, that.class_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(class_cd, site_cd);
    }
}
