/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package concurrent;
import java.util.List;
import java.util.Collection;

public interface ExecutorService extends Executor {

    void shutdown();
    // shutdown at once
    List<Runnable> shutdownNow();

    /**
     * statue is shutdown
     */
    boolean isShutdown();

    /**
     * status is terminated
     */
    boolean isTerminated();

    /**
     * wait until run over
     */
    boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * submit a execution
     */
    <T> Future<T> submit(Callable<T> task);

    /**
     * task over, and return the result that was given
     */
    <T> Future<T> submit(Runnable task, T result);

    /**
     * just submit
     */
    Future<?> submit(Runnable task);

    /**
     * run tasks and return results
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException;

    /**
     * run tasks with timeout
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * tasks
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException;

    /**
     * tasks with timeout
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
