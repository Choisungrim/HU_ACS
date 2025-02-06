package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.CarrierMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(CarrierMasterId.class)
@Entity
@Table(name = "acs_carrier_master")
@Getter
@Setter
@ToString
public class CarrierMaster {

    @Id
    private String carrier_id; // 대차 명칭

    private String carrier_tp; // 대차 타입(lot, rolltainer, pallet)

    private String status_tx; // 대차 상태 (운영가능한)

    @Id
    private String port_id; // 포트 명칭

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

    public CarrierMaster() {
    }

    public CarrierMaster(String carrier_id, String port_id, String site_cd) {
        this.carrier_id = carrier_id;
        this.port_id = port_id;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrierMaster that = (CarrierMaster) o;
        return Objects.equals(carrier_id, that.carrier_id) && Objects.equals(port_id, that.port_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carrier_id, port_id, site_cd);
    }
}
