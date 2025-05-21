//package com.hubis.acs.scheduler;
//
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.scheduling.config.Task;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class TaskScheduler {
//
//    private static final Logger log = LoggerFactory.getLogger(TaskScheduler.class);
//
//    private final TaskService taskService;
//    private final TaskRepository taskRepository;
//
//    @Scheduled(fixedRate = 10000) // 10초마다 실행
//    public void assignAndExecuteTask() {
//        try {
//            Optional<Task> optionalTask = taskRepository.findFirstByStatusOrderByCreatedAtAsc("WAITING");
//
//            if (optionalTask.isEmpty()) {
//                return; // 처리할 작업 없음
//            }
//
//            Task task = optionalTask.get();
//
//            // 상태 업데이트 (예: WAITING → RUNNING)
//            task.setStatus("RUNNING");
//            taskRepository.save(task);
//
//            // 실제 처리 로직 실행
//            taskService.assignAndExecute(task.getId());
//
//            // 완료 처리
//            task.setStatus("COMPLETED");
//            taskRepository.save(task);
//
//        } catch (Exception e) {
//            log.error("스케줄러 작업 실행 실패", e);
//        }
//    }
//}
