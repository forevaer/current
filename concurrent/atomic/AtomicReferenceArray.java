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
import java.util.Arrays;
import java.lang.reflect.Array;
import sun.misc.Unsafe;

public class AtomicReferenceArray<E> implements java.io.Serializable {
    private static final long serialVersionUID = -6209656149925076980L;

    private static final Unsafe unsafe;
    private static final int base;
    private static final int shift;
    private static final long arrayFieldOffset;
    private final Object[] array; // must have exact type Object[]

    static {
        try {
            unsafe = Unsafe.getUnsafe();
            arrayFieldOffset = unsafe.objectFieldOffset
                (AtomicReferenceArray.class.getDeclaredField("array"));
            base = unsafe.arrayBaseOffset(Object[].class);
            int scale = unsafe.arrayIndexScale(Object[].class);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            shift = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private long checkedByteOffset(int i) {
        if (i < 0 || i >= array.length)
            throw new IndexOutOfBoundsException("index " + i);

        return byteOffset(i);
    }

    private static long byteOffset(int i) {
        return ((long) i << shift) + base;
    }

    /**
     * length
     */
    public AtomicReferenceArray(int length) {
        array = new Object[length];
    }

    /**
     * set array
     */
    public AtomicReferenceArray(E[] array) {
        // Visibility guaranteed by final field guarantees
        this.array = Arrays.copyOf(array, array.length, Object[].class);
    }

    /**
     * length
     */
    public final int length() {
        return array.length;
    }

    /**
     * array[index]
     */
    public final E get(int i) {
        return getRaw(checkedByteOffset(i));
    }

    /**
     * get(object, offset)
     */
    @SuppressWarnings("unchecked")
    private E getRaw(long offset) {
        return (E) unsafe.getObjectVolatile(array, offset);
    }

    /**
     * array[i] = newValue
     */
    public final void set(int i, E newValue) {
        unsafe.putObjectVolatile(array, checkedByteOffset(i), newValue);
    }

    /**
     * lazySet
     */
    public final void lazySet(int i, E newValue) {
        unsafe.putOrderedObject(array, checkedByteOffset(i), newValue);
    }

    @SuppressWarnings("unchecked")
    public final E getAndSet(int i, E newValue) {
        return (E)unsafe.getAndSetObject(array, checkedByteOffset(i), newValue);
    }

    public final boolean compareAndSet(int i, E expect, E update) {
        return compareAndSetRaw(checkedByteOffset(i), expect, update);
    }

    private boolean compareAndSetRaw(long offset, E expect, E update) {
        return unsafe.compareAndSwapObject(array, offset, expect, update);
    }

    public final boolean weakCompareAndSet(int i, E expect, E update) {
        return compareAndSet(i, expect, update);
    }

    public final E getAndUpdate(int i, UnaryOperator<E> updateFunction) {
        long offset = checkedByteOffset(i);
        E prev, next;
        do {
            prev = getRaw(offset);
            next = updateFunction.apply(prev);
        } while (!compareAndSetRaw(offset, prev, next));
        return prev;
    }

    public final E updateAndGet(int i, UnaryOperator<E> updateFunction) {
        long offset = checkedByteOffset(i);
        E prev, next;
        do {
            prev = getRaw(offset);
            next = updateFunction.apply(prev);
        } while (!compareAndSetRaw(offset, prev, next));
        return next;
    }

    public final E getAndAccumulate(int i, E x,
                                    BinaryOperator<E> accumulatorFunction) {
        long offset = checkedByteOffset(i);
        E prev, next;
        do {
            prev = getRaw(offset);
            next = accumulatorFunction.apply(prev, x);
        } while (!compareAndSetRaw(offset, prev, next));
        return prev;
    }

    public final E accumulateAndGet(int i, E x,
                                    BinaryOperator<E> accumulatorFunction) {
        long offset = checkedByteOffset(i);
        E prev, next;
        do {
            prev = getRaw(offset);
            next = accumulatorFunction.apply(prev, x);
        } while (!compareAndSetRaw(offset, prev, next));
        return next;
    }

    public String toString() {
        return Arrays.toString(array);
    }

    /**
     * set by objectStream
     */
    private void readObject(java.io.ObjectInputStream s) throws Exception{
        Object a = s.readFields().get("array", null);
        if (a == null || !a.getClass().isArray())
            throw new java.io.InvalidObjectException("Not array type");
        if (a.getClass() != Object[].class)
            a = Arrays.copyOf((Object[])a, Array.getLength(a), Object[].class);
        unsafe.putObjectVolatile(this, arrayFieldOffset, a);
    }

}
