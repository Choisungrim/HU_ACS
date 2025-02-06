package com.hubis.acs.common.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private static final ConcurrentHashMap<String, String> responseMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();

    public static void waitForResponse(String workId, String transactionId, long timeoutMillis) {
        Object lock = new Object();
        lockMap.put(transactionId, lock);
        synchronized (lock) {
            long endTime = System.currentTimeMillis() + timeoutMillis; // 종료 시간 계산
            while (!responseMap.containsKey(transactionId)) {
                long waitTime = endTime - System.currentTimeMillis(); // 남은 대기 시간 계산
                logger.info(String.format("%s work waiting %s ms", workId, waitTime));
                if (waitTime <= 0) {
                    // Timeout 시간이 지났으면 대기 상태 종료
                    break;
                }
                try {
                    lock.wait(waitTime); // 남은 시간만큼 대기
                } catch (InterruptedException e) {
                    logger.info(String.format("%s work waiting timeOut %s ms", workId, waitTime));
                    Thread.currentThread().interrupt(); // 스레드의 인터럽트 상태 설정
                    break;
                }
            }
        }
        lockMap.remove(transactionId); // 사용 완료된 lock 제거
    }

    public static void completeResponse(String workId, String transactionId, String response) {
        responseMap.put(transactionId, response);
        Object lock = lockMap.get(transactionId);
        if (lock != null) {
            synchronized (lock) {
                logger.info(String.format("%s work complete ReTurnCode => %s", workId, response));
                //lock.notifyAll(); // 응답을 기다리는 스레드 깨우기
                lock.notify(); // 응답을 기다리는 스레드 깨우기
            }
        }
    }

    public static String getResponse(String transactionId) {
        try {
            return responseMap.remove(transactionId); // 응답 가져오기
        } finally {
            lockMap.remove(transactionId); // 사용 완료된 lock 제거
        }
    }
}
