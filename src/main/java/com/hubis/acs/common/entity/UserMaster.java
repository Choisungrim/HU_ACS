package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.UserMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(UserMasterId.class)
@Entity
@Table(name = "acs_user_master")
@Getter
@Setter
@ToString
public class UserMaster {

    @Id
    private String account_id; // 

    private String user_nm; // 

    private String password_tx; // 암호화된 비밀번호

    private String email_nm; // 

    private String role_cd; // 그룹에 할당된 역할 코드

    private String class_cd; // 유저가 속한 그룹정보

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

    public UserMaster() {
    }

    public UserMaster(String account_id, String site_cd) {
        this.account_id = account_id;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserMaster that = (UserMaster) o;
        return Objects.equals(account_id, that.account_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account_id, site_cd);
    }
}
