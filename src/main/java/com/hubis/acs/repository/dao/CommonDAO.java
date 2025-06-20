package com.hubis.acs.repository.dao;

import com.hubis.acs.common.utils.TimeUtils;
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
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class CommonDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private static final AtomicInteger histSequence = new AtomicInteger(0);
    private static final int MAX_SEQUENCE = 1000;


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
            insertHistory(entity);
            entityManager.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // CommonDAO.java

    @SuppressWarnings("unchecked")
    public <T> boolean merge(T entity) {
        try {
            entityManager.merge(entity);
            insertHistory(entity);
            entityManager.flush();
            return true;
        } catch (Exception e) {
            System.err.println("merge failed for entity: "+ entity.getClass().getSimpleName() +""+ e);
            return false;
        }
    }


    //데이터Hist 생성
    private <T> void insertHistory(T entity) {
        String className = entity.getClass().getSimpleName();
        String histClassName;

        if (className.toLowerCase().contains("master")) {
            histClassName = className.replace("Master", "Hist");
        } else {
            histClassName = className + "Hist";
        }

        String histPackage = entity.getClass().getPackage().getName();
        String fullHistClassName = histPackage + "." + histClassName;

        if (!doesClassExist(fullHistClassName)) {
            // Hist 클래스가 존재하지 않으면 기록하지 않고 return
            return;
        }

        try {
            Class<?> histClass = Class.forName(fullHistClassName);
            Object histEntity = histClass.getDeclaredConstructor().newInstance();

            org.springframework.beans.BeanUtils.copyProperties(entity, histEntity);

            // hist_id 값 수동으로 세팅
            for (Field field : histClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(jakarta.persistence.Id.class)
                        && field.getName().equalsIgnoreCase("hist_id")) {
                    field.setAccessible(true);
                    field.set(histEntity, generateUniqueHistId()); // 예시 값
                    break;
                }
            }

            entityManager.persist(histEntity);
        } catch (Exception e) { System.err.println("insertHistory failed for " + entity.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // 데이터 수정
    @SuppressWarnings("unchecked")
    public <T> boolean update(T entity) {
        try {
            entityManager.merge(entity);
            insertHistory(entity);
            entityManager.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 데이터 삭제
    public <T> boolean delete(Class<T> clazz, Object id) {
        T entity = selectOneById(clazz, id);
        if (entity != null) {
            insertHistory(entity);
            entityManager.remove(entity);
            entityManager.flush();
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

    private boolean doesClassExist(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private long generateUniqueHistId() {
        long base = System.currentTimeMillis();
        int sequence = histSequence.getAndIncrement() % MAX_SEQUENCE;
        return base * MAX_SEQUENCE + sequence;
    }
}
