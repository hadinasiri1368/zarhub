package org.zarhub.repository;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.zarhub.common.Utils;
import org.zarhub.constant.Consts;
import org.zarhub.constant.OperationType;
import org.zarhub.constant.TimeFormat;
import org.zarhub.exception.GeneralExceptionType;
import org.zarhub.exception.ZarHubException;
import org.zarhub.model.BaseEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class JpaRepository {
    @Value("${spring.jpa.show-manual-log}")
    private Boolean showManualLog;

    @PersistenceContext()
    private EntityManager entityManager;

    @Transactional
    public <ENTITY> void save(ENTITY entity, Long userId, String uuid) throws Exception {
        setNullToId(entity);
        ((BaseEntity) entity).setInsertedUserId(userId);
        ((BaseEntity) entity).setInsertedDateTime(new Date());

        Utils.setNull(entity);
        entityManager.persist(entity);
        logEntityFields(entity, OperationType.INSERT, userId, uuid);
    }

    @Transactional
    public <ENTITY> void update(ENTITY entity, Long userId, String uuid) throws Exception {
        if (Utils.isNull(getId(entity)))
            throw new ZarHubException(GeneralExceptionType.ID_IS_NULL);
        ((BaseEntity) entity).setUpdatedUserId(userId);
        ((BaseEntity) entity).setUpdatedDateTime(new Date());

        Utils.setNull(entity);
        entityManager.merge(entity);
        logEntityFields(entity, OperationType.UPDATE, userId, uuid);
    }

    @Transactional
    public <ENTITY> void remove(ENTITY entity, Long userId, String uuid) throws Exception {
        logEntityFields(entity, OperationType.DELETE, userId, uuid);
        entityManager.remove(entityManager.merge(entity));
    }

    @Transactional
    public <ENTITY, ID> void removeById(Class<ENTITY> entityClass, ID id, Long userId, String uuid) throws Exception {
        ENTITY entity = findOne(entityClass, id);
        remove(entity, userId, uuid);
    }

    @Transactional
    public int executeUpdate(String hql, Map<String, Object> param, Long userId, String uuid) {
        Query query = entityManager.createQuery(hql);
        if (!Utils.isNull(param) && !param.isEmpty()) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        logQueryWithParameters(hql, param, userId, uuid);
        int returnValue = query.executeUpdate();
        clearCache();
        return returnValue;
    }

    @Transactional
    public int nativeExecuteUpdate(String sql, Map<String, Object> param, Long userId, String uuid) {
        Query query = entityManager.createNativeQuery(sql);
        if (!Utils.isNull(param) && !param.isEmpty()) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        logQueryWithParameters(sql, param, userId, uuid);
        int returnValue = query.executeUpdate();
        clearCache();
        return returnValue;
    }

    @Transactional
    public int nativeExecuteUpdate(String sql, Long userId, String uuid) {
        Query query = entityManager.createNativeQuery(sql);
        logQueryWithParameters(sql, null, userId, uuid);
        int returnValue = query.executeUpdate();
        clearCache();
        return returnValue;
    }

    @Transactional
    public int executeUpdate(String hql, Long userId, String uuid) {
        return executeUpdate(hql, null, userId, uuid);
    }

    @Transactional
    public <ENTITY> void batchInsert(List<ENTITY> entities, Long userId, String uuid) throws Exception {
        if (Utils.isNull(entities) || entities.size() == 0)
            return;
        int batchSize = Consts.JPA_BATCH_SIZE;
        for (int i = 0; i < entities.size(); i++) {
            save(entities.get(i), userId, uuid);
//            entityManager.persist(entities.get(i));

            if (i > 0 && i % batchSize == 0) {
                clearCache();
            }
        }

        clearCache();
    }

    @Transactional
    public <ENTITY> void batchUpdate(List<ENTITY> entities, Long userId, String uuid) throws Exception {
        if (Utils.isNull(entities) || entities.size() == 0)
            return;
        int batchSize = Consts.JPA_BATCH_SIZE;
        for (int i = 0; i < entities.size(); i++) {
//            entityManager.merge(entities.get(i));
            update(entities.get(i), userId, uuid);

            if (i > 0 && i % batchSize == 0) {
                clearCache();
            }
        }

        clearCache();
    }

    @Transactional
    public <ENTITY> void batchRemove(List<ENTITY> entities, Long userId, String uuid) throws Exception {
        if (Utils.isNull(entities) || entities.size() == 0)
            return;
        int batchSize = Consts.JPA_BATCH_SIZE;
        for (int i = 0; i < entities.size(); i++) {
            remove(entities.get(i), userId, uuid);

            if (i > 0 && i % batchSize == 0) {
                clearCache();
            }
        }
        clearCache();
        ;
    }

    public <ENTITY, ID> ENTITY findOne(Class<ENTITY> entityClass, ID id) {
        ENTITY entity = entityManager.find(entityClass, id);
        return entity;
    }

    public <ENTITY> Page<ENTITY> findAll(Class<ENTITY> entityClass, Pageable pageable) {
        Entity entity = entityClass.getAnnotation(Entity.class);
        Query query = entityManager.createQuery("select entity from " + entity.name() + " entity");
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<ENTITY> resultList = query.getResultList();
        long totalCount = getTotalCount(entity.name());
        return new PageImpl<>(resultList, pageable, totalCount);
    }

    public <ENTITY> List<ENTITY> findAll(Class<ENTITY> entityClass) {
        Entity entity = entityClass.getAnnotation(Entity.class);
        Query query = entityManager.createQuery("select entity from " + entity.name() + " entity");
        List<ENTITY> resultList = query.getResultList();
        return resultList;
    }

    public List listObjectByQuery(String hql, Map<String, Object> param) {
        Query query = entityManager.createQuery(hql);
        if (!Utils.isNull(param) && param.size() > 0)
            for (String key : param.keySet()) {
                query.setParameter(key, param.get(key));
            }
        return query.getResultList();
    }

    public List<Map<String, Object>> listMapByQuery(String hql, Map<String, Object> param) {
        TypedQuery<Tuple> query = entityManager.createQuery(hql, Tuple.class);
        if (!Utils.isNull(param) && param.size() > 0)
            for (String key : param.keySet()) {
                query.setParameter(key, param.get(key));
            }
        return query.getResultList().stream()
                .map(tuple -> {
                    Map<String, Object> rowMap = new HashMap<>();
                    for (TupleElement<?> element : tuple.getElements()) {
                        rowMap.put(element.getAlias(), tuple.get(element.getAlias()));
                    }
                    return rowMap;
                })
                .collect(Collectors.toList());
    }

    public Page<Map<String, Object>> listMapByQuery(String hql, Map<String, Object> param, Pageable pageable) {
        TypedQuery<Tuple> query = entityManager.createQuery(hql, Tuple.class);
        if (!Utils.isNull(param) && !param.isEmpty()) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        long countResult = getTotalCount(query);
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);
        List<Map<String, Object>> resultList = query.getResultList().stream()
                .map(tuple -> {
                    Map<String, Object> rowMap = new HashMap<>();
                    for (TupleElement<?> element : tuple.getElements()) {
                        rowMap.put(element.getAlias(), tuple.get(element.getAlias()));
                    }
                    return rowMap;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(resultList, pageable, countResult);
    }

    public Page<Object> listObjectByQuery(String hql, Map<String, Object> param, Pageable pageable) {
        Query query = entityManager.createQuery(hql);
        if (!Utils.isNull(param) && !param.isEmpty()) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        long countResult = getTotalCount(query);
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);
        List<Object> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, countResult);
    }

    public Long getLongValue(String sql, Map<String, Object> param) {
        Query query = entityManager.createQuery(sql);
        if (!Utils.isNull(param) && !param.isEmpty()) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return (Long) query.getSingleResult();
    }

    public Long getLongValue(String sql) {
        return getLongValue(sql, null);
    }

    public String getStringValue(String sql, Map<String, Object> param) {
        Query query = entityManager.createQuery(sql);
        if (!Utils.isNull(param) && !param.isEmpty()) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return (String) query.getSingleResult();
    }

    public String getStringValue(String sql) {
        return getStringValue(sql, null);
    }

    private long getTotalCount(String entityName) {
        Query countQuery = entityManager.createQuery("select count(1) from " + entityName + " entity");
        return (long) countQuery.getSingleResult();
    }

    private long getTotalCount(Query query) {
        String queryString = query.unwrap(org.hibernate.query.Query.class).getQueryString();

        int orderByIndex = queryString.toLowerCase().indexOf("order by");
        if (orderByIndex != -1) {
            queryString = queryString.substring(0, orderByIndex);
        }

        String countQueryString = "select count(1) from (" + queryString + ") tbl_count";
        Query countQuery = entityManager.createQuery(countQueryString);

        query.getParameters().forEach(param -> {
            Object value = query.getParameterValue(param);
            countQuery.setParameter(param.getName(), value);
        });
        return (long) countQuery.getSingleResult();
    }

    private <ENTITY> void logEntityFields(ENTITY entity, OperationType operation, Long userId, String uuid) {
        if (!showManualLog)
            return;

        String currentTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Consts.GREGORIAN_DATE_FORMAT + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));

        if (entity == null) {
            log.info("Entity is null. UserId: {}, Time: {}, uuid: {}", userId, currentTime, uuid);
            return;
        }

        Class<?> entityClass = entity.getClass();
        StringBuilder logMessage = new StringBuilder(operation.getValue() + " entity : ")
                .append(entityClass.getAnnotation(Entity.class).name()).append(" [");

        Field[] fields = entityClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                logMessage.append("id").append("=").append(getId(entity)).append(", ");
                logMessage.append(field.getName()).append("=").append(value);
            } catch (Exception e) {
                logMessage.append(field.getName()).append("=ACCESS_ERROR");
            }
            if (i < fields.length - 1) {
                logMessage.append(", ");
            }
        }

        logMessage.append("]");

        String finalLogMessage = String.format("%s | UserId: %d | Time: %s | uuid: %s",
                logMessage, userId, currentTime, uuid);

        log.info(finalLogMessage);
    }


    private void logQueryWithParameters(String sql, Map<String, Object> param, Long userId, String uuid) {
        if (!showManualLog)
            return;

        String currentTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Consts.GREGORIAN_DATE_FORMAT + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));

        StringBuilder logMessage = new StringBuilder(sql);
        if (param != null && !param.isEmpty()) {
            logMessage.append(" with parameters: [");
            param.forEach((key, value) -> logMessage.append(key).append("=").append(value).append(", "));
            logMessage.setLength(logMessage.length() - 2);
            logMessage.append("]");
        }

        logMessage.append(" | ")
                .append("UserId: ")
                .append(userId)
                .append(" | ")
                .append("Time: ")
                .append(currentTime)
                .append(" | ")
                .append("uuid: ")
                .append(uuid);

        log.info(logMessage.toString());
    }

    private void clearCache() {
        entityManager.flush();
        entityManager.clear();
    }

    private <ENTITY> void setNullToId(ENTITY entity) throws Exception {
        Method m = entity.getClass().getMethod("setId", Long.class);
        m.invoke(entity, (Long) null);
    }

    private <ENTITY> Long getId(ENTITY entity) throws Exception {
        Method m = entity.getClass().getMethod("getId");
        return (Long) m.invoke(entity);
    }
}
