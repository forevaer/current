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

/**
 * Interfaces and classes providing a framework for locking and waiting
 * for conditions that is distinct from built-in synchronization and
 * monitors.  The framework permits much greater flexibility in the use of
 * locks and conditions, at the expense of more awkward syntax.
 *
 * <p>The {@link concurrent.locks.Lock} interface supports
 * locking disciplines that differ in semantics (reentrant, fair, etc),
 * and that can be used in non-block-structured contexts including
 * hand-over-hand and lock reordering algorithms.  The main implementation
 * is {@link concurrent.locks.ReentrantLock}.
 *
 * <p>The {@link concurrent.locks.ReadWriteLock} interface
 * similarly defines locks that may be shared among readers but are
 * exclusive to writers.  Only a single implementation, {@link
 * concurrent.locks.ReentrantReadWriteLock}, is provided, since
 * it covers most standard usage contexts.  But programmers may create
 * their own implementations to cover nonstandard requirements.
 *
 * <p>The {@link concurrent.locks.Condition} interface
 * describes condition variables that may be associated with Locks.
 * These are similar in usage to the implicit monitors accessed using
 * {@code Object.wait}, but offer extended capabilities.
 * In particular, multiple {@code Condition} objects may be associated
 * with a single {@code Lock}.  To avoid compatibility issues, the
 * names of {@code Condition} methods are different from the
 * corresponding {@code Object} versions.
 *
 * <p>The {@link concurrent.locks.AbstractQueuedSynchronizer}
 * class serves as a useful superclass for defining locks and other
 * synchronizers that rely on queuing blocked threads.  The {@link
 * concurrent.locks.AbstractQueuedLongSynchronizer} class
 * provides the same functionality but extends support to 64 bits of
 * synchronization state.  Both extend class {@link
 * concurrent.locks.AbstractOwnableSynchronizer}, a simple
 * class that helps record the thread currently holding exclusive
 * synchronization.  The {@link concurrent.locks.LockSupport}
 * class provides lower-level blocking and unblocking support that is
 * useful for those developers implementing their own customized lock
 * classes.
 *
 * @since 1.5
 */
package concurrent.locks;
