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

package concurrent.atomic;
import java.util.function.LongUnaryOperator;
import java.util.function.LongBinaryOperator;
import sun.misc.Unsafe;

public class AtomicLong extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 1927816293512124184L;

    // unsafe
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    // offset
    private static final long valueOffset;

    static final boolean VM_SUPPORTS_LONG_CAS = VMSupportsCS8();

    /**
     * java8 cas
     */
    private static native boolean VMSupportsCS8();

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                (AtomicLong.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }
    // value
    private volatile long value;

    /**
     * set value
     */
    public AtomicLong(long initialValue) {
        value = initialValue;
    }

    /**
     * constructor without param
     */
    public AtomicLong() {
    }

    /**
     * get value
     */
    public final long get() {
        return value;
    }

    /**
     * set
     */
    public final void set(long newValue) {
        value = newValue;
    }

    /**
     * default set is on memory
     * this is by JVM, lower
     */
    public final void lazySet(long newValue) {
        unsafe.putOrderedLong(this, valueOffset, newValue);
    }

    public final long getAndSet(long newValue) {
        return unsafe.getAndSetLong(this, valueOffset, newValue);
    }

    public final boolean compareAndSet(long expect, long update) {
        return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
    }

    public final boolean weakCompareAndSet(long expect, long update) {
        return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
    }


    public final long getAndIncrement() {
        return unsafe.getAndAddLong(this, valueOffset, 1L);
    }


    public final long getAndDecrement() {
        return unsafe.getAndAddLong(this, valueOffset, -1L);
    }


    public final long getAndAdd(long delta) {
        return unsafe.getAndAddLong(this, valueOffset, delta);
    }

    public final long incrementAndGet() {
        return unsafe.getAndAddLong(this, valueOffset, 1L) + 1L;
    }

    public final long decrementAndGet() {
        return unsafe.getAndAddLong(this, valueOffset, -1L) - 1L;
    }

    public final long addAndGet(long delta) {
        return unsafe.getAndAddLong(this, valueOffset, delta) + delta;
    }

    public final long getAndUpdate(LongUnaryOperator updateFunction) {
        long prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsLong(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public final long updateAndGet(LongUnaryOperator updateFunction) {
        long prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsLong(prev);
        } while (!compareAndSet(prev, next));
        return next;
    }

    public final long getAndAccumulate(long x,
                                       LongBinaryOperator accumulatorFunction) {
        long prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsLong(prev, x);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public final long accumulateAndGet(long x,
                                       LongBinaryOperator accumulatorFunction) {
        long prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsLong(prev, x);
        } while (!compareAndSet(prev, next));
        return next;
    }


    public String toString() {
        return Long.toString(get());
    }

    public int intValue() {
        return (int)get();
    }

    public long longValue() {
        return get();
    }

    public float floatValue() {
        return (float)get();
    }

    public double doubleValue() {
        return (double)get();
    }

}
