package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class RobotMasterId implements Serializable {

    private String robot_id;

    private String site_cd;

    public RobotMasterId() { }
    public RobotMasterId(String robot_id, String site_cd) {this.robot_id = robot_id;this.site_cd = site_cd; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotMasterId that = (RobotMasterId) o;
        return Objects.equals(robot_id, that.robot_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(robot_id, site_cd);
    }
}
