package com.hubis.acs.service;

import com.hubis.acs.common.entity.vo.EventInfo;

import java.util.List;

public interface BaseService {

    <T> T findById(Class<T> clazz, Object id);

    <T> T findByEntity(Class<T> clazz, T id);

    <T> List<T> findByField(Class<T> clazz, String field, Object value);

    <T> List<T> findByConditions(Class<T> clazz, T example);

    <T> boolean save(EventInfo eventInfo, T entity);

    <T> boolean saveOrUpdate(EventInfo eventInfo, T entity);


    <T> boolean update(EventInfo eventInfo, T entity);

    <T> boolean delete(Class<T> clazz, Object id);
}
