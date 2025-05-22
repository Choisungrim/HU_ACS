package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_login_hist")
@Getter
@Setter
@ToString
public class LoginHist extends BaseEntity {

    @Id
    private Long hist_id; // 일련 번호

    private String user_nm; // 사용자 이름

    private String role_cd; // 역할 코드

    private java.time.LocalDateTime access_by; // 접속 시간

    private String site_cd; // SITE 정보

    public LoginHist() {
    }

    public LoginHist(Long hist_id) {
        this.hist_id = hist_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginHist that = (LoginHist) o;
        return Objects.equals(hist_id, that.hist_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hist_id);
    }
}
