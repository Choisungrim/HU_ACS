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
public class UserclassMaster {

    @Id
    private String class_cd; // 그룹 코드

    private String class_nm; // 그룹 명칭

    private String role_cd; // 그룹에 할당된 역할 코드

    private boolean usable_fl; // 데이터 사용 가능 여부

    @Id
    private String site_cd; // SITE 정보

    private String description_tx; // 데이터에 대한 설명

    private String prev_activity_tx; // 이전 활동 내용

    private String activity_tx; // 현재 활동 내용

    private String creator_by; // 데이터 생성자

    private java.time.LocalDateTime create_at; // 생성 시간

    private String modifier_by; // 데이터 수정자

    private java.time.LocalDateTime modify_at; // 수정 시간

    private String trans_tx; // 관련 트랜잭션 ID

    private java.time.LocalDateTime last_event_at; // 최근 이벤트 발생 시간

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
