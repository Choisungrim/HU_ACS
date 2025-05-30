package com.hubis.acs.common.cache;

import com.hubis.acs.common.entity.SiteMaster;
import com.hubis.acs.service.BaseService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SiteCache {

    private final BaseService baseService;

    @Getter
    private Set<String> siteCdSet = new HashSet<>();


    public SiteCache(BaseService baseService) {
        this.baseService = baseService;
    }

    @PostConstruct
    public void init() {
        load();
    }

    public void load() {
        List<SiteMaster> sites = baseService.findByConditions(SiteMaster.class, new SiteMaster());
        this.siteCdSet = sites.stream()
                .map(SiteMaster::getSite_cd)
                .collect(Collectors.toSet()); // 중복 자동 제거
    }

    public void reload() {
        load();
    }

    public boolean contains(String siteCd) {
        return siteCdSet.contains(siteCd);
    }

}
