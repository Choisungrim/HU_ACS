package com.hubis.acs.common.configuration;

import org.springframework.messaging.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkQueue {
    private BlockingQueue<Message<?>> queue = new LinkedBlockingQueue<>();

    public void enqueue(Message<?> message) throws InterruptedException {
        queue.put(message);
    }

    public Message<?> dequeue() throws InterruptedException {
        return queue.take();
    }
}
