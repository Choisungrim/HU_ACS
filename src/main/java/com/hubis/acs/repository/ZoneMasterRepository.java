package com.hubis.acs.repository;

import com.hubis.acs.common.entity.ZoneMaster;
import com.hubis.acs.common.entity.vo.ZoneMasterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ZoneMasterRepository extends JpaRepository<ZoneMaster, ZoneMasterId> {

    // map_uuid로 필터링 (복합키 일부만 기준)
    @Query("SELECT z FROM ZoneMaster z WHERE z.map_uuid = :mapUuid AND z.usable_fl = 1")
    List<ZoneMaster> findByMapUuid(Long mapUuid);

    // site_cd 기준으로 영역 조회
    @Query("SELECT z FROM ZoneMaster z WHERE z.site_cd = :siteCd AND z.usable_fl = 1")
    List<ZoneMaster> findUsableZonesBySite(String siteCd);

    // 특정 ZoneId와 Map UUID로 조회
    @Query("SELECT z FROM ZoneMaster z WHERE z.zone_id = :zoneId AND z.map_uuid = :mapUuid AND z.site_cd = :siteCd")
    ZoneMaster findByKey(String zoneId, Long mapUuid, String siteCd);
}

