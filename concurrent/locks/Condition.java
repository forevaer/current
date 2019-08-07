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

package concurrent.locks;
import concurrent.TimeUnit;
import java.util.Date;

public interface Condition {
    // park
    void await() throws InterruptedException;

    void awaitUninterruptibly();
    // park for time
    long awaitNanos(long nanosTimeout) throws InterruptedException;
    // park for time
    boolean await(long time, TimeUnit unit) throws InterruptedException;

    boolean awaitUntil(Date deadline) throws InterruptedException;
    // notify
    void signal();
    // notify all
    void signalAll();
}
