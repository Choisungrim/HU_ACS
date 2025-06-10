package com.hubis.acs.service.impl;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.BaseEntity;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.repository.dao.CommonDAO;
import com.hubis.acs.service.BaseService;
import jakarta.persistence.IdClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class BaseServiceImpl implements BaseService {

    private final CommonDAO commonDAO;

    @Override
    public <T> T findById(Class<T> clazz, Object id) {
        return commonDAO.selectOneById(clazz, id);
    }

    @Override
    public <T> T findByEntity(Class<T> clazz, T entity) {
        return commonDAO.selectOne(clazz, entity);
    }

    @Override
    public <T> List<T> findByField(Class<T> clazz, String field, Object value) {
        return commonDAO.selectList(clazz, field, value);
    }

    @Override
    public <T> List<T> findByConditions(Class<T> clazz, T example) {
        return commonDAO.selectList(clazz, example);
    }

    @Override
    @Transactional
    public <T> boolean save(EventInfo eventInfo, T entity ) {
        setCommonModule(eventInfo, entity, false);
        return commonDAO.insert(entity);
    }

    @Override
    @Transactional
    public <T> boolean saveOrUpdate(EventInfo eventInfo, T entity ) {
        Object primaryKey = extractPrimaryKey(entity);  // 유틸 메서드 필요
        boolean exists = findById(entity.getClass(), primaryKey) != null;

        setCommonModule(eventInfo, entity, !exists);

        if (exists) {
            return commonDAO.update(entity);
        } else {
            return commonDAO.insert(entity);
        }
    }

    @Override
    @Transactional
    public <T> boolean delete(Class<T> clazz, Object id) {
        return commonDAO.delete(clazz, id);
    }

    private void saveByHist(){

    }
    private void setCommonModule(EventInfo eventInfo, Object entity,  boolean isNew) {
        if (!(entity instanceof BaseEntity)) return;

        BaseEntity base = (BaseEntity) entity;
        base.setUsable_fl(BaseConstants.Usable.USABLE);
        base.setTrans_tx(eventInfo.getTransactionId());
        base.setActivity_tx(eventInfo.getActivity());
        base.setLast_event_at(java.time.LocalDateTime.now());
        base.setModifier_by(eventInfo.getUserId());
        base.setModify_at(java.time.LocalDateTime.now());

        if (base.getActivity_tx() != null && !base.getActivity_tx().isEmpty())
            base.setPrev_activity_tx(base.getActivity_tx());

        if (isNew) {
            base.setCreator_by(eventInfo.getUserId());
            base.setCreate_at(java.time.LocalDateTime.now());
        }
    }

    private Object extractPrimaryKey(Object entity) {
        try {
            Class<?> entityClass = entity.getClass();
            if (entityClass.isAnnotationPresent(IdClass.class)) {
                Class<?> idClass = entityClass.getAnnotation(IdClass.class).value();
                Object idObj = idClass.getDeclaredConstructor().newInstance();

                for (java.lang.reflect.Field idField : idClass.getDeclaredFields()) {
                    idField.setAccessible(true);
                    java.lang.reflect.Field entityField = entityClass.getDeclaredField(idField.getName());
                    entityField.setAccessible(true);
                    Object value = entityField.get(entity);
                    idField.set(idObj, value);
                }

                return idObj;
            } else {
                // 단일 키
                for (java.lang.reflect.Field field : entityClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                        field.setAccessible(true);
                        return field.get(entity);
                    }
                }
            }

            throw new IllegalArgumentException("ID 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new RuntimeException("복합키 추출 실패", e);
        }
    }

}
