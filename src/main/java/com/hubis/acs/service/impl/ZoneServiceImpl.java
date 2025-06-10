package com.hubis.acs.service.impl;

import com.hubis.acs.common.entity.ZoneMaster;
import com.hubis.acs.common.position.handler.GlobalZoneManager;
import com.hubis.acs.common.position.model.GlobalZone;
import com.hubis.acs.common.position.util.ZoneConverter;
import com.hubis.acs.middleware.work.PositionChange;
import com.hubis.acs.repository.ZoneMasterRepository;
import com.hubis.acs.service.BaseService;
import com.hubis.acs.service.ZoneService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {
    private static final Logger logger = LoggerFactory.getLogger(ZoneServiceImpl.class);
    private final ZoneMasterRepository zoneRepo;
    private final GlobalZoneManager zoneManager;
    private final BaseService baseService;

    @PostConstruct
    public void init() {
        loadAllZones();
    }

    @Override
    public void loadAllZones() {
        List<ZoneMaster> allZones = zoneRepo.findAll();
        allZones.stream()
                .filter(z -> "block".equalsIgnoreCase(z.getZone_tp()))
                .collect(Collectors.groupingBy(ZoneMaster::getMap_uuid))
                .forEach((mapUuid, zoneList) -> {
                    List<GlobalZone> globalZones = zoneList.stream()
                            .map(ZoneConverter::fromEntity)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    zoneManager.registerZones(mapUuid, globalZones);

                    logger.info("▶▶ map_uuid = {}, loaded zones = {}", mapUuid,
                            globalZones.stream()
                                    .map(gz -> String.format("%s [%.1f~%.1f, %.1f~%.1f]",
                                            gz.getZoneId(),
                                            gz.getMinX(), gz.getMaxX(),
                                            gz.getMinY(), gz.getMaxY()))
                                    .collect(Collectors.joining("; ")));

                });

        logger.info("✅ total zone count: {}", allZones.size());

    }


    @Override
    public void reloadZonesByMap(Long mapUuid) {
        List<ZoneMaster> zones = zoneRepo.findByMapUuid(mapUuid);
        List<GlobalZone> globalZones = zones.stream()
                .map(ZoneConverter::fromEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        zoneManager.registerZones(mapUuid, globalZones);
    }
}
