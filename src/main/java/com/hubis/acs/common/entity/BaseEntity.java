package com.hubis.acs.common.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(name = "usable_fl", columnDefinition = "TINYINT(1)")
    private Boolean usable_fl;
    private String description_tx;
    private String prev_activity_tx;
    private String activity_tx;
    private String creator_by;
    private LocalDateTime create_at;
    private String modifier_by;
    private LocalDateTime modify_at;
    private String trans_tx;
    private LocalDateTime last_event_at;
}
