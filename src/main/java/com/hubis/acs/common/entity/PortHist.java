package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_port_hist")
@Getter
@Setter
@ToString
public class PortHist {

    @Id
    private Long hist_id; // 일련 번호

    private String port_id; // 

    private String port_tp; // 

    private String status_tx; // 

    private String equipment_id; // 

    private String node_id; // 

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)") 
    private Boolean usable_fl; // 데이터 사용 가능 여부

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

    public PortHist() {
    }

    public PortHist(Long hist_id) {
        this.hist_id = hist_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortHist that = (PortHist) o;
        return Objects.equals(hist_id, that.hist_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hist_id);
    }
}
