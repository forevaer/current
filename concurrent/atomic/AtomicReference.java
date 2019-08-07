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
import java.util.function.UnaryOperator;
import java.util.function.BinaryOperator;
import sun.misc.Unsafe;

public class AtomicReference<V> implements java.io.Serializable {
    private static final long serialVersionUID = -1848883965231344442L;
    // unsafe
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    // offset
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                (AtomicReference.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }
    // value
    private volatile V value;

    /**
     * init
     */
    public AtomicReference(V initialValue) {
        value = initialValue;
    }

    /**
     * construct
     */
    public AtomicReference() {
    }

    /**
     * value
     */
    public final V get() {
        return value;
    }

    /**
     * set
     */
    public final void set(V newValue) {
        value = newValue;
    }

    /**
     * set by jvm
     */
    public final void lazySet(V newValue) {
        unsafe.putOrderedObject(this, valueOffset, newValue);
    }

    /**
     * object_cas
     */
    public final boolean compareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    /**
     * object_cas
     */
    public final boolean weakCompareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    /**
     * value = get()
     * set(newValue)
     * return value
     */
    @SuppressWarnings("unchecked")
    public final V getAndSet(V newValue) {
        return (V)unsafe.getAndSetObject(this, valueOffset, newValue);
    }

    /**
     * value = get()
     * cas_set(func(value))
     * return value
     */
    public final V getAndUpdate(UnaryOperator<V> updateFunction) {
        V prev, next;
        do {
            prev = get();
            next = updateFunction.apply(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    /**
     *  get(cas_set(get(), func(get())))
     */
    public final V updateAndGet(UnaryOperator<V> updateFunction) {
        V prev, next;
        do {
            prev = get();
            next = updateFunction.apply(prev);
        } while (!compareAndSet(prev, next));
        return next;
    }

    /**
     * value = get()
     * cas_set(func(x, value))
     * return value
     */
    public final V getAndAccumulate(V x,
                                    BinaryOperator<V> accumulatorFunction) {
        V prev, next;
        do {
            prev = get();
            next = accumulatorFunction.apply(prev, x);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    /**
     * get(cas_set(func(x, get())))
     */
    public final V accumulateAndGet(V x,
                                    BinaryOperator<V> accumulatorFunction) {
        V prev, next;
        do {
            prev = get();
            next = accumulatorFunction.apply(prev, x);
        } while (!compareAndSet(prev, next));
        return next;
    }

    /**
     * toString
     */
    public String toString() {
        return String.valueOf(get());
    }

}
