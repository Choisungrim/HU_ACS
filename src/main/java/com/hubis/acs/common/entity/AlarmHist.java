package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.AlarmMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_alarm_hist")
@Getter
@Setter
@ToString
public class AlarmHist extends BaseEntity {

    @Id
    private Long hist_id; // 일련 번호

    private String alarm_cd; // 영역 ID

    private String alarm_nm; // 영역 명칭

    private String alarm_tp; // 영역 타입

    private String alarm_lv; // 영역 포인트 개수

    private String alarm_val; // 포인트 정보

    private String site_cd; // SITE 정보

    public AlarmHist() {
    }

    public AlarmHist(Long hist_id) {
        this.hist_id = hist_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmHist that = (AlarmHist) o;
        return Objects.equals(hist_id, that.hist_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hist_id);
    }
}
