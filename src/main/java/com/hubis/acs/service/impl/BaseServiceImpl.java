package com.hubis.acs.service.impl;

import com.hubis.acs.common.entity.BaseEntity;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.repository.dao.CommonDAO;
import com.hubis.acs.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("BaseService")
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
        setCommonModule(eventInfo, entity, true);

        if (!commonDAO.insert(entity)) {
            setCommonModule(eventInfo, entity, false);
            return commonDAO.update(entity);
        }
        return true;
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
        base.setUsable_fl(true);
        base.setTrans_tx(eventInfo.getTransactionId());
        base.setActivity_tx(eventInfo.getActivity());
        base.setLast_event_at(java.time.LocalDateTime.now());
        base.setModifier_by(eventInfo.getUserId());
        base.setModify_at(java.time.LocalDateTime.now());

        if(base.getActivity_tx() != null && base.getActivity_tx()!="")
            base.setPrev_activity_tx(base.getActivity_tx());

        if (isNew) {
            base.setCreator_by(eventInfo.getUserId());
            base.setCreate_at(java.time.LocalDateTime.now());
        }
    }
}
