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
public class RoleMaster {

    @Id
    private String role_cd; // 역할 코드

    private String role_nm; // 역할 이름

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)") 
    private Boolean usable_fl; // 데이터 사용 가능 여부

    @Id
    private String site_cd; // SITE 정보

    private String description_tx; // 

    private String prev_activity_tx; // 

    private String activity_tx; // 

    private String creator_by; // 

    private java.time.LocalDateTime create_at; // 생성 시간

    private String modifier_by; // 

    private java.time.LocalDateTime modify_at; // 수정 시간

    private String trans_tx; // 

    private java.time.LocalDateTime last_event_at; // 최근 이벤트 발생 시간

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
