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

    private String port_tp; // 

    private String status_tx; // 

    @Id
    private String equipment_id; // 포트의 설비

    private String node_id; // 포트의 위치정보

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
