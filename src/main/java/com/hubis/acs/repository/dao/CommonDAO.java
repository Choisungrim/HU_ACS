package com.hubis.acs.repository.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CommonDAO {

    @Autowired
    private EntityManager entityManager;

    // 단일 조회
    public <T> T selectOne(Class<T> clazz, Object id) {
        return entityManager.find(clazz, id);
    }

    // 다중 조회
    public <T> List<T> selectList(Class<T> clazz, String field, Object value) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        if (field != null && value != null) {
            Predicate predicate = cb.equal(root.get(field), value);
            query.select(root).where(predicate);
        } else {
            query.select(root);
        }

        return entityManager.createQuery(query).getResultList();
    }

    // 데이터 생성
    @Transactional
    public <T> void insert(T entity) {
        entityManager.persist(entity);
    }

    // 데이터 수정
    @Transactional
    public <T> void update(T entity) {
        entityManager.merge(entity);
    }

    // 데이터 삭제
    @Transactional
    public <T> void delete(Class<T> clazz, Object id) {
        T entity = selectOne(clazz, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
