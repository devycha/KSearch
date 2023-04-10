package com.ksearch.back.lock;

import com.ksearch.back.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedissonLockEventStockFacade {
    private final RedissonClient redissonClient;
    private final EventService eventService;

    public void decrease(Long eventId) {
        RLock lock = redissonClient.getLock("event::" + eventId.toString());

        try {
            boolean available = lock.tryLock(1, 1, TimeUnit.SECONDS);

            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }

            eventService.participateEvent(eventId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}

