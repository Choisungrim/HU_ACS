package com.hubis.acs.common.position.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubis.acs.common.cache.BaseConstantCache;
import com.hubis.acs.common.entity.ConstMaster;
import com.hubis.acs.common.position.model.Point;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MapTransformManager {
    @Value("${site.cd}")
    private String siteCd;

    private static final Logger logger = LoggerFactory.getLogger(MapTransformManager.class);

    private static class MapTransform {
        private final Point localOriginInGlobal;
        private final double rotationDeg;
        private final double resolution;

        public MapTransform(Point localOriginInGlobal, double rotationDeg, double resolution) {
            this.localOriginInGlobal = localOriginInGlobal;
            this.rotationDeg = rotationDeg;
            this.resolution = resolution;
        }

        public Point toGlobal(Point local) {
            double scaledX = local.getX() * resolution;
            double scaledY = local.getY() * resolution;

            double theta = Math.toRadians(rotationDeg);
            double x = scaledX * Math.cos(theta) - scaledY * Math.sin(theta);
            double y = scaledX * Math.sin(theta) + scaledY * Math.cos(theta);

            return new Point(x + localOriginInGlobal.getX(), y + localOriginInGlobal.getY());
        }

        public Point toLocal(Point global) {
            double dx = global.getX() - localOriginInGlobal.getX();
            double dy = global.getY() - localOriginInGlobal.getY();

            double theta = Math.toRadians(-rotationDeg);
            double x = dx * Math.cos(theta) - dy * Math.sin(theta);
            double y = dx * Math.sin(theta) + dy * Math.cos(theta);

            return new Point(x / resolution, y / resolution);
        }
    }

    private final Map<String, MapTransform> transformByModel = new HashMap<>();
    private final BaseConstantCache constantCache;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MapTransformManager(BaseConstantCache constantCache) {
        this.constantCache = constantCache;
    }

    @PostConstruct
    public void init() {
        logger.info("[MapTransformManager] Initializing for siteCd={}", siteCd);

        for (Map.Entry<String, ConstMaster> entry : constantCache.getConstMap().entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(siteCd + "::TRANSFORM::")) continue;

            String modelNm = entry.getValue().getConstant_cd();
            String constVal = entry.getValue().getConstant_val();

            try {
                // JSON 파싱
                TransformConfig config = objectMapper.readValue(constVal, TransformConfig.class);

                registerTransform(modelNm, new Point(config.originX, config.originY), config.rotationDeg, config.resolution);

                logger.info("[MapTransformManager] Registered transform for model {} → Origin({},{}), Rot={}, Res={}",
                        modelNm, config.originX, config.originY, config.rotationDeg, config.resolution);

            } catch (Exception e) {
                logger.error("[MapTransformManager] Failed to parse TRANSFORM for model {}: {}", modelNm, constVal, e);
            }
        }
    }

    public void registerTransform(String modelNm, Point origin, double rotationDeg, double resolution) {
        transformByModel.put(modelNm, new MapTransform(origin, rotationDeg, resolution));
    }

    public Point toGlobal(String modelNm, Point localPoint) {
        MapTransform transform = findTransformForModel(modelNm);
        return transform.toGlobal(localPoint);
    }

    public Point toLocal(String modelNm, Point globalPoint) {
        MapTransform transform = findTransformForModel(modelNm);
        return transform.toLocal(globalPoint);
    }

    private MapTransform findTransformForModel(String modelNm) {
        for (Map.Entry<String, MapTransform> entry : transformByModel.entrySet()) {
            String manufacturer = entry.getKey();
            if (modelNm.contains(manufacturer)) {  // 또는 startsWith(manufacturer) 도 가능
                return entry.getValue();
            }
        }
        // default fallback
        return defaultTransform();
    }

    private MapTransform defaultTransform() {
        return new MapTransform(new Point(0, 0), 0.0, 1.0); // 기본은 mm 기준 그대로
    }
}
