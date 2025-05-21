package com.hubis.acs.repository.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    // 단일 조회
    public <T> T selectOneById(Class<T> clazz, Object primaryId) {
        return entityManager.find(clazz, primaryId);
    }

    public <T> T selectOne(Class<T> clazz, T example) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        Predicate predicate = cb.conjunction(); // 기본: true (AND 조건 시작)

        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            // static, final 필드 제외
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
                    java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            try {
                Object value = field.get(example);
                if (value != null) {
                    predicate = cb.and(predicate, cb.equal(root.get(field.getName()), value));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Reflection 오류", e);
            }
        }

        if (predicate.getExpressions().isEmpty()) {
            throw new IllegalArgumentException("조건 없는 selectOne은 허용되지 않습니다.");
        }

        query.where(predicate);

        List<T> result = entityManager.createQuery(query)
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
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

    public <T> List<T> selectList(Class<T> clazz, T example) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        Predicate predicate = cb.conjunction(); // 기본적으로 AND 조건

        // 객체의 필드 값을 동적으로 조회 조건으로 추가
        for (java.lang.reflect.Field field : example.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true); // private 필드 접근 허용
                Object value = field.get(example);

                if (value != null) { // null이 아닌 경우만 필터 적용
                    predicate = cb.and(predicate, cb.equal(root.get(field.getName()), value));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Reflection 오류", e);
            }
        }

        query.where(predicate);
        return entityManager.createQuery(query).getResultList();
    }

    // 데이터 생성
    @SuppressWarnings("unchecked")
    public <T> boolean insert(T entity) {
        Class<T> clazz = (Class<T>) entity.getClass(); // 타입 안정성을 확보
        if (selectOne(clazz, entity) != null) {
            return false;
        }
        entityManager.persist(entity);
        return true;
    }

    // 데이터 수정
    @SuppressWarnings("unchecked")
    public <T> boolean update(T entity) {
        Class<T> clazz = (Class<T>) entity.getClass();
        if (selectOne(clazz, entity) == null) {
            return false;
        }
        entityManager.merge(entity);
        return true;
    }

    // 데이터 삭제
    public <T> boolean delete(Class<T> clazz, Object id) {
        T entity = selectOneById(clazz, id);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }
}
