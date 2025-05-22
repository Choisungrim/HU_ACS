package com.hubis.acs.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "acs_carrier_hist")
@Getter
@Setter
@ToString
public class CarrierHist extends BaseEntity {

    @Id
    private Long hist_id; // 일련 번호

    private String carrier_id; // carrier ID

    private String carrier_tp; // carrier 타입

    private String status_tx; // 상태

    private String site_cd; // SITE 정보

    public CarrierHist() {
    }

    public CarrierHist(Long hist_id) {
        this.hist_id = hist_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrierHist that = (CarrierHist) o;
        return Objects.equals(hist_id, that.hist_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hist_id);
    }
}
