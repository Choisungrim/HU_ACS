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
public class PortHist extends BaseEntity {

    @Id
    private Long hist_id; // 일련 번호

    private String port_id; // 포트 명칭

    private String port_tp; // 포트 타입

    private String status_tx; // 상태

    private String equipment_id; // 포트의 설비

    private String node_id; // 포트의 위치정보

    private String site_cd; // SITE 정보

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
