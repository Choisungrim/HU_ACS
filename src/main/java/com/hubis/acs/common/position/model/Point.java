package com.hubis.acs.common.position.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Point extends Position{
    public Point() {
        super(0, 0);  // 기본값
    }

    public Point(double x, double y) {
        super(x, y);
    }

}
