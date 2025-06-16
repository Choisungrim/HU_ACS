package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class AlarmMasterId implements Serializable {

    private String alarm_cd ;

    private String site_cd;

    public AlarmMasterId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmMasterId that = (AlarmMasterId) o;
        return Objects.equals(alarm_cd, that.alarm_cd) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarm_cd, site_cd);
    }
}
