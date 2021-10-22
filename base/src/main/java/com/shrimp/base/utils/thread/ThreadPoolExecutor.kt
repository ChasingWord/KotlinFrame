package com.shrimp.base.utils.thread

import android.os.Process
import android.util.Log
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by chasing on 2021/10/22.
 */
class ThreadPoolExecutor(
    corePoolSize: Int, maximumPoolSize: Int, keepAlive: Long, timeUnit: TimeUnit?,
    threadFactory: ThreadFactory, private val uncaughtThrowableStrategy: UncaughtThrowableStrategy
) : java.util.concurrent.ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAlive,
    timeUnit,
    PriorityBlockingQueue(), threadFactory
) {
    companion object {
        const val TAG = "PriorityExecutor"
    }

    private val ordering = AtomicInteger()

    /**
     * A strategy for handling unexpected and uncaught throwables thrown by futures run on the pool.
     */
    enum class UncaughtThrowableStrategy {
        /**
         * Silently catches and ignores the uncaught throwables.
         */
        IGNORE,

        /**
         * Logs the uncaught throwables using [.TAG] and [Log].
         */
        LOG {
            override fun handle(t: Throwable?) {
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, "Request threw uncaught throwable", t)
                }
            }
        },

        /**
         * Rethrows the uncaught throwables to crash the app.
         */
        THROW {
            override fun handle(t: Throwable?) {
                super.handle(t)
                throw RuntimeException(t)
            }
        };

        open fun handle(t: Throwable?) {
            // Ignore.
        }
    }

    /**
     * Constructor to build a fixed thread pool with the given pool size using
     *
     * @param poolSize The number of threads.
     */
    constructor(poolSize: Int) : this(poolSize, UncaughtThrowableStrategy.LOG)

    /**
     * Constructor to build a fixed thread pool with the given pool size using
     *
     * @param poolSize                  The number of threads.
     * @param uncaughtThrowableStrategy Dictates how the pool should handle uncaught and unexpected throwables
     * thrown by Futures run by the pool.
     */
    constructor(poolSize: Int, uncaughtThrowableStrategy: UncaughtThrowableStrategy) : this(
        poolSize,
        poolSize,
        0,
        TimeUnit.MILLISECONDS,
        DefaultThreadFactory(),
        uncaughtThrowableStrategy
    )

    override fun <T> newTaskFor(runnable: Runnable?, value: T): RunnableFuture<T> {
        return LoadTask(runnable, value, ordering.getAndIncrement())
    }

    override fun afterExecute(r: Runnable?, t: Throwable?) {
        super.afterExecute(r, t)
        if (t == null && r is Future<*>) {
            val future = r as Future<*>
            if (future.isDone && !future.isCancelled) {
                try {
                    future.get()
                } catch (e: InterruptedException) {
                    uncaughtThrowableStrategy.handle(e)
                } catch (e: ExecutionException) {
                    uncaughtThrowableStrategy.handle(e)
                }
            }
        }
    }

    /**
     * A [ThreadFactory] that builds threads with priority
     * [android.os.Process.THREAD_PRIORITY_BACKGROUND].
     */
    class DefaultThreadFactory : ThreadFactory {
        var threadNum = 0
        override fun newThread(runnable: Runnable): Thread {
            val result: Thread = object : Thread(
                runnable,
                "fifo-pool-thread-$threadNum"
            ) {
                override fun run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
                    super.run()
                }
            }
            threadNum++
            return result
        }
    }

    // Visible for testing.
    internal class LoadTask<T>(runnable: Runnable?, result: T, order: Int) :
        FutureTask<T>(runnable, result), Comparable<LoadTask<*>?> {
        private var priority = 0
        private val order: Int
        override fun equals(other: Any?): Boolean {
            if (other is LoadTask<*>) {
                return order == other.order && priority == other.priority
            }
            return false
        }

        override fun hashCode(): Int {
            var result = priority
            result = 31 * result + order
            return result
        }

        override operator fun compareTo(other: LoadTask<*>?): Int {
            var result = priority - (other?.priority ?: 0)
            if (result == 0) {
                result = order - (other?.order ?: 0)
            }
            return result
        }

        init {
            priority = if (runnable !is Prioritized) 0 else (runnable as Prioritized).getPriority()
            this.order = order
        }
    }
}
