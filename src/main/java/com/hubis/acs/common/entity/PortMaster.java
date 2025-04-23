package com.hubis.acs.common.entity;
import com.hubis.acs.common.entity.vo.PortMasterId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@IdClass(PortMasterId.class)
@Entity
@Table(name = "acs_port_master")
@Getter
@Setter
@ToString
public class PortMaster {

    @Id
    private String port_id; // 포트 명칭

    private String port_tp; // 포트 타입

    private String status_tx; // 상태

    @Id
    private String equipment_id; // 포트의 설비

    private String node_id; // 포트의 위치정보

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)") 
    private Boolean usable_fl; // 데이터 사용 가능 여부

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

    public PortMaster() {
    }

    public PortMaster(String port_id, String equipment_id, String site_cd) {
        this.port_id = port_id;
        this.equipment_id = equipment_id;
        this.site_cd = site_cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortMaster that = (PortMaster) o;
        return Objects.equals(port_id, that.port_id) && Objects.equals(equipment_id, that.equipment_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port_id, equipment_id, site_cd);
    }
}
