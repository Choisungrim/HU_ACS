package com.hubis.acs.common.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class MicroTransferControlId implements Serializable {

    private String micro_transfer_id;

    private String transfer_id;

    private String site_cd;

    public MicroTransferControlId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MicroTransferControlId that = (MicroTransferControlId) o;
        return Objects.equals(micro_transfer_id, that.micro_transfer_id) && Objects.equals(transfer_id, that.transfer_id) && Objects.equals(site_cd, that.site_cd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(micro_transfer_id, transfer_id, site_cd);
    }
}
