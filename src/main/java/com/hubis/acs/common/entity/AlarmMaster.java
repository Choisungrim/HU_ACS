package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.AlarmMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(AlarmMasterId.class)
@Entity
@Table(name = "acs_alarm_master")
@Getter
@Setter
@ToString
public class AlarmMaster extends BaseEntity {

    @Id
    private String alarm_cd; // 영역 ID

    private String alarm_nm; // 영역 명칭

    private String alarm_tp; // 영역 타입

    private String alarm_lv; // 영역 포인트 개수

    private String alarm_val; // 포인트 정보

    @Id
    private String site_cd; // SITE 정보

    public AlarmMaster() {
    }

    public AlarmMaster(String alarm_cd, String site_cd) {
        this.alarm_cd = alarm_cd;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmMaster that = (AlarmMaster) o;
        return Objects.equals(alarm_cd, that.alarm_cd) && Objects.equals(alarm_nm, that.alarm_nm) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarm_cd, site_cd);
    }
}
