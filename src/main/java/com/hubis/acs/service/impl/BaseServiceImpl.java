package com.hubis.acs.service.impl;

import com.hubis.acs.repository.dao.CommonDAO;
import com.hubis.acs.service.BaseService;
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
    public <T> boolean save(T entity) {
        return commonDAO.insert(entity);
    }

    @Override
    @Transactional
    public <T> void  saveOrUpdate(T entity) {
        if (!commonDAO.insert(entity)) {
            commonDAO.update(entity);
        }
    }

    @Override
    @Transactional
    public <T> boolean delete(Class<T> clazz, Object id) {
        return commonDAO.delete(clazz, id);
    }
}
