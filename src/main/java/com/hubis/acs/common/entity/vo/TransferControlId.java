package com.hubis.acs.common.entity.vo;

import java.io.Serializable;
import java.util.Objects;

public class TransferControlId implements Serializable {

    private String transfer_id;

    private String site_cd;

    public TransferControlId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferControlId that = (TransferControlId) o;
        return Objects.equals(transfer_id, that.transfer_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transfer_id, site_cd);
    }
}
