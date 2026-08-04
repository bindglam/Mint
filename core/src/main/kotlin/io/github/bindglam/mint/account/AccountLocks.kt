package io.github.bindglam.mint.account

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

object AccountLocks {
    private val locks = ConcurrentHashMap<UUID, ReentrantLock>()

    fun <T> withLock(uuid: UUID, action: () -> T): T {
        val lock = locks.computeIfAbsent(uuid) { ReentrantLock() }

        lock.lock()
        try {
            return action()
        } finally {
            lock.unlock()
            // Clean up unused locks to prevent memory leaks
            if (!lock.isLocked && !lock.hasQueuedThreads()) {
                locks.remove(uuid, lock)
            }
        }
    }

    fun clearAll() {
        locks.clear()
    }
}
