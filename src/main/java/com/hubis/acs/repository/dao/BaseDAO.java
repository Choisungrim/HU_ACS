//package com.hubis.acs.repository.dao;
//
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.support.SqlSessionDaoSupport;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Map;
//
//public abstract class BaseDAO extends SqlSessionDaoSupport {
//
//    /**
//     * Annotation 형식으로 sqlSession(SqlSessionFactoryBean)을 받아와
//     * 이를 super(SqlSessionDaoSupport)의 setSqlSessionFactory 메서드를 호출하여 설정해 준다.
//     * <p>
//     * SqlSessionTemplate이 지정된 경우된 경우 이 SqlSessionFactory가 무시된다. (Batch 처리를 위해서는 SqlSessionTemplate가 필요)
//     *
//     *
//     * @param sqlSession SqlSessionFactory로 MyBatis와의 연계를 위한 기본 클래스
//     */
//    @Resource(name = "sqlSession")
//    public void setSqlSessionFactory(SqlSessionFactory sqlSession) {
//    	super.setSqlSessionFactory(sqlSession);
//    }
//
//    @Resource(name = "sqlSessionTemplate")
//    public void setsqlSessionTemplate(SqlSessionTemplate sqlSession) {
//    	super.setSqlSessionTemplate(sqlSession);
//    }
//
//    /**
//     * 입력 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId -  입력 처리 SQL mapping 쿼리 ID
//     *
//     * @return DBMS가 지원하는 경우 insert 적용 결과 count
//     */
//    public int insert(String queryId) {
//    	return getSqlSessionTemplate().insert(queryId);
//    }
//
//    /**
//     * 입력 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId -  입력 처리 SQL mapping 쿼리 ID
//     * @param parameterObject - 입력 처리 SQL mapping 입력 데이터를 세팅한 파라메터 객체(보통 VO 또는 Map)
//     *
//     * @return DBMS가 지원하는 경우 insert 적용 결과 count
//     */
//    public int insert(String queryId, Object parameterObject) {
//    	return getSqlSessionTemplate().insert(queryId, parameterObject);
//    }
//
//    /**
//     * 수정 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId - 수정 처리 SQL mapping 쿼리 ID
//     *
//     * @return DBMS가 지원하는 경우 update 적용 결과 count
//     */
//    public int update(String queryId) {
//        return getSqlSessionTemplate().update(queryId);
//    }
//
//    /**
//     * 수정 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId - 수정 처리 SQL mapping 쿼리 ID
//     * @param parameterObject - 수정 처리 SQL mapping 입력 데이터(key 조건 및 변경 데이터)를 세팅한 파라메터 객체(보통 VO 또는 Map)
//     *
//     * @return DBMS가 지원하는 경우 update 적용 결과 count
//     */
//    public int update(String queryId, Object parameterObject) {
//        return getSqlSessionTemplate().update(queryId, parameterObject);
//    }
//
//    /**
//     * 삭제 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId - 삭제 처리 SQL mapping 쿼리 ID
//     *
//     * @return DBMS가 지원하는 경우 delete 적용 결과 count
//     */
//    public int delete(String queryId) {
//        return getSqlSessionTemplate().delete(queryId);
//    }
//
//    /**
//     * 삭제 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId - 삭제 처리 SQL mapping 쿼리 ID
//     * @param parameterObject - 삭제 처리 SQL mapping 입력 데이터(일반적으로 key 조건)를  세팅한 파라메터 객체(보통 VO 또는 Map)
//     *
//     * @return DBMS가 지원하는 경우 delete 적용 결과 count
//     */
//    public int delete(String queryId, Object parameterObject) {
//
//        return getSqlSessionTemplate().delete(queryId, parameterObject);
//    }
//
//    /**
//     * 단건조회 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId - 단건 조회 처리 SQL mapping 쿼리 ID
//     *
//     * @return 결과 객체 - SQL mapping 파일에서 지정한 resultType/resultMap 에 의한 단일 결과 객체(보통 VO 또는 Map)
//     */
//    public <T> T selectOne(String queryId) {
//    	try {
//    		return getSqlSessionTemplate().selectOne(queryId);
//    	}
//    	catch (Exception e) {
//			// TODO: handle exception
//		}
//
//    	return null;
//    }
//
//    /**
//     * 단건조회 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId - 단건 조회 처리 SQL mapping 쿼리 ID
//     * @param parameterObject - 단건 조회 처리 SQL mapping 입력 데이터(key)를 세팅한 파라메터 객체(보통 VO 또는 Map)
//     *
//     * @return 결과 객체 - SQL mapping 파일에서 지정한 resultType/resultMap 에 의한 단일 결과 객체(보통 VO 또는 Map)
//     */
//    public <T> T selectOne(String queryId, Object parameterObject) {
//    	try {
//    		return getSqlSessionTemplate().selectOne(queryId, parameterObject);
//    	}
//    	catch (Exception e) {
//			// TODO: handle exception
//            logger.error(String.format("Error executing selectOne: queryId=%s, parameterObject=%s, message=%s",queryId,parameterObject.toString(),e.getMessage()));
//            e.printStackTrace();
//		}
//
//    	return null;
//    }
//
//    /**
//     * 결과 목록을 Map 을 변환한다.
//     * 모든 구문이 파라미터를 필요로 하지는 않기 때문에, 파라미터 객체를 요구하지 않는 형태로 오버로드되었다.
//     *
//     * @param queryId - 단건 조회 처리 SQL mapping 쿼리 ID
//     * @param mapKey - 결과 객체의 프로퍼티 중 하나를 키로 사용
//     *
//     * @return 결과 객체 - SQL mapping 파일에서 지정한 resultType/resultMap 에 의한 단일 결과 객체(보통 VO 또는 Map)의 Map
//     */
//    public <K, V> Map<K, V> selectMap(String queryId, String mapKey) {
//    	try {
//    		return getSqlSessionTemplate().selectMap(queryId, mapKey);
//    	}
//    	catch (Exception e) {
//			// TODO: handle exception
//		}
//
//    	return null;
//    }
//
//    /**
//     * 결과 목록을 Map 을 변환한다.
//     * 모든 구문이 파라미터를 필요로 하지는 않기 때문에, 파라미터 객체를 요구하지 않는 형태로 오버로드되었다.
//     *
//     * @param queryId - 단건 조회 처리 SQL mapping 쿼리 ID
//     * @param parameterObject - 맵 조회 처리 SQL mapping 입력 데이터(조회 조건)를 세팅한 파라메터 객체(보통 VO 또는 Map)
//     * @param mapKey - 결과 객체의 프로퍼티 중 하나를 키로 사용
//     *
//     * @return 결과 객체 - SQL mapping 파일에서 지정한 resultType/resultMap 에 의한 단일 결과 객체(보통 VO 또는 Map)의 Map
//     */
//    public <K, V> Map<K, V> selectMap(String queryId, Object parameterObject, String mapKey) {
//    	try {
//    		return getSqlSessionTemplate().selectMap(queryId, parameterObject, mapKey);
//    	}
//    	catch (Exception e) {
//			// TODO: handle exception
//		}
//
//    	return null;
//    }
//
//    /**
//     * 리스트 조회 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId - 리스트 조회 처리 SQL mapping 쿼리 ID
//     *
//     * @return 결과 List 객체 - SQL mapping 파일에서 지정한  resultType/resultMap 에 의한 결과 객체(보통 VO 또는 Map)의 List
//     */
//	public <E> List<E> selectList(String queryId) {
//		try {
//			return getSqlSessionTemplate().selectList(queryId);
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		return null;
//    }
//
//	/**
//     * 리스트 조회 처리 SQL mapping 을 실행한다.
//     *
//     * @param queryId - 리스트 조회 처리 SQL mapping 쿼리 ID
//     * @param parameterObject - 리스트 조회 처리 SQL mapping 입력 데이터(조회 조건)를 세팅한 파라메터 객체(보통 VO 또는 Map)
//     *
//     * @return 결과 List 객체 - SQL mapping 파일에서 지정한  resultType/resultMap 에 의한 결과 객체(보통 VO 또는 Map)의 List
//     */
//	public <E> List<E> selectList(String queryId, Object parameterObject) {
//		try {
//
//			return getSqlSessionTemplate().selectList(queryId, parameterObject);
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		return null;
//    }
//
//}
