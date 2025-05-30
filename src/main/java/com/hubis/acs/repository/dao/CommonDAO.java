package com.hubis.acs.repository.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
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

        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            try {
                Object value = field.get(example);

                if(!isInvalidValue(value, field))
                    continue;

                predicate = cb.and(predicate, cb.equal(root.get(field.getName()), value));

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

            if (predicate.getExpressions().isEmpty()) {
                throw new IllegalArgumentException("조건 없는 selectList은 허용되지 않습니다.");
            }

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
        for (Field field : getAllFields(clazz)) {
            try {
                field.setAccessible(true); // private 필드 접근 허용
                Object value = field.get(example);

                if(!isInvalidValue(value, field))
                    continue;

                predicate = cb.and(predicate, cb.equal(root.get(field.getName()), value));
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
        try {
            entityManager.persist(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 데이터 수정
    @SuppressWarnings("unchecked")
    public <T> boolean update(T entity) {
        try {
            entityManager.merge(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
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


    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private boolean isInvalidValue(Object value, Field field)
    {
        if (Modifier.isStatic(field.getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) {
            return false;
        }
        // 1. null 체크
        if (value == null) return false;

        // 2. primitive 기본값 체크 (예: int = 0, double = 0.0)
        if (value instanceof Number) {
            double numberValue = ((Number) value).doubleValue();
            if (numberValue == 0.0) return false;
        }

        // 3. 빈 문자열 제외 (선택사항)
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            return false;
        }

        return true;
    }
}
