package com.hubis.acs.common.utils;

import com.hubis.acs.common.entity.NodeMaster;
import com.hubis.acs.common.entity.PortMaster;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.service.BaseService;

public class RuntimeUtils {

    public static NodeMaster getSourceNodeFromTransfer(TransferControl transfer, BaseService baseService) {
        PortMaster portId = new PortMaster();
        portId.setPort_id(transfer.getSource_port_id());
        portId.setSite_cd(transfer.getSite_cd());

        PortMaster port = baseService.findByEntity(PortMaster.class, portId);
        if (port == null) {
            System.out.println("[ERROR] Port 정보 없음: " + transfer.getSource_port_id() + " / " + transfer.getSite_cd());
            return null;
        }

        NodeMaster sourceNodeId = new NodeMaster();
        sourceNodeId.setNode_id(port.getNode_id());
        sourceNodeId.setSite_cd(port.getSite_cd());

        NodeMaster sourceNode = baseService.findByEntity(NodeMaster.class, sourceNodeId);
        if (sourceNode == null) {
            System.out.println("[ERROR] Node 정보 없음: " + port.getNode_id());
        }
        return sourceNode;
    }

    public static NodeMaster getDestNodeFromTransfer(TransferControl transfer, BaseService baseService) {
        PortMaster portId = new PortMaster();
        portId.setPort_id(transfer.getDestination_port_id());
        portId.setSite_cd(transfer.getSite_cd());

        PortMaster port = baseService.findByEntity(PortMaster.class, portId);
        if (port == null) {
            System.out.println("[ERROR] Port 정보 없음: " + transfer.getDestination_port_id() + " / " + transfer.getSite_cd());
            return null;
        }

        NodeMaster destNodeId = new NodeMaster();
        destNodeId.setNode_id(port.getNode_id());
        destNodeId.setSite_cd(port.getSite_cd());

        NodeMaster destNode = baseService.findByEntity(NodeMaster.class, destNodeId);
        if (destNode == null) {
            System.out.println("[ERROR] Node 정보 없음: " + port.getNode_id());
        }
        return destNode;
    }

}
