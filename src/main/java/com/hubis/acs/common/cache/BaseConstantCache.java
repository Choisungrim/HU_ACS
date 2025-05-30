package com.hubis.acs.common.cache;

import com.hubis.acs.common.entity.ConstMaster;
import com.hubis.acs.service.BaseService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BaseConstantCache {
    private static final Logger log = LoggerFactory.getLogger(BaseConstantCache.class);

    private final BaseService baseService;

    // key: constant_tp + "::" + constant_cd
    @Getter
    private final Map<String, ConstMaster> constMap = new ConcurrentHashMap<>();

    public BaseConstantCache(BaseService baseService) {
        this.baseService = baseService;
    }

    @PostConstruct
    public void init() {
        load();
        log.info("BaseConstantCache initialized. Loaded {} constants.", constMap.size());
    }

    private void load() {
        ConstMaster consta = new ConstMaster(); // 조건 없이 전체 조회
        List<ConstMaster> constList = baseService.findByConditions(ConstMaster.class, consta);

        for (ConstMaster constEntry : constList) {
            String key = generateKey(constEntry.getSite_cd(), constEntry.getConstant_tp(), constEntry.getConstant_cd());
            constMap.put(key, constEntry);
        }
    }

    public void reload() {
        constMap.clear();
        load();
        log.info("BaseConstantCache reloaded.");
    }

    public ConstMaster get(String siteCd, String constantTp, String constantCd) {
        return constMap.get(generateKey(siteCd, constantTp, constantCd));
    }

    private String generateKey(String siteCd, String constantTp, String constantCd) {
        return siteCd + "::" + constantTp + "::" + constantCd;
    }
}
