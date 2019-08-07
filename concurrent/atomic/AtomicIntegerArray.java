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
import java.util.Arrays;
import java.util.function.IntUnaryOperator;
import java.util.function.IntBinaryOperator;
import sun.misc.Unsafe;


public class AtomicIntegerArray implements java.io.Serializable {
    private static final long serialVersionUID = 2862133569453604235L;
    // unsafe
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    // baseOffset
    private static final int base = unsafe.arrayBaseOffset(int[].class);
    // offset
    private static final int shift;
    // data
    private final int[] array;

    static {
        int scale = unsafe.arrayIndexScale(int[].class);
        // 2的pow, 详情见 https://blog.csdn.net/wait_for_eva/article/details/96758602
        if ((scale & (scale - 1)) != 0)
            throw new Error("data type scale not a power of two");
        // scale 开头多少个 0
        shift = 31 - Integer.numberOfLeadingZeros(scale);
    }

    // 得到第 i 元素的内存地址
    private long checkedByteOffset(int i) {
        if (i < 0 || i >= array.length)
            throw new IndexOutOfBoundsException("index " + i);

        return byteOffset(i);
    }

    private static long byteOffset(int i) {
        // int = 1 byte = 8 bit
        // shift = byte
        // base : 到头
        // shift: 移位
        return ((long) i << shift) + base;
    }

    /**
     * 指定数组的长度
     */
    public AtomicIntegerArray(int length) {
        array = new int[length];
    }

    /**
     * 浅拷贝赋值
     */
    public AtomicIntegerArray(int[] array) {
        this.array = array.clone();
    }

    /**
     * 数组长度
     */
    public final int length() {
        return array.length;
    }

    /**
     * 获取指定坐标元素
     */
    public final int get(int i) {
        return getRaw(checkedByteOffset(i));
    }

    /**
     *  指定对象，指定地址数据
     */
    private int getRaw(long offset) {
        return unsafe.getIntVolatile(array, offset);
    }

    /**
     * array[i] = newValue
     * 本地设置，刷新主存
     */
    public final void set(int i, int newValue) {
        unsafe.putIntVolatile(array, checkedByteOffset(i), newValue);
    }

    /**
     * array[i] = newValue
     * 直接设置
     */
    public final void lazySet(int i, int newValue) {
        unsafe.putOrderedInt(array, checkedByteOffset(i), newValue);
    }

    /**
     * getAndSet
     */
    public final int getAndSet(int i, int newValue) {
        return unsafe.getAndSetInt(array, checkedByteOffset(i), newValue);
    }

    /**
     * cas by index
     */
    public final boolean compareAndSet(int i, int expect, int update) {
        // cas by offset
        return compareAndSetRaw(checkedByteOffset(i), expect, update);
    }

    /**
     * position :
     *      object
     *      index
     * cas
     *      expect
     *      update
     */
    private boolean compareAndSetRaw(long offset, int expect, int update) {
        return unsafe.compareAndSwapInt(array, offset, expect, update);
    }

    /**
     * similar as cas
     */
    public final boolean weakCompareAndSet(int i, int expect, int update) {
        return compareAndSet(i, expect, update);
    }

    /**
     * getAndAdd(1)
     */
    public final int getAndIncrement(int i) {
        return getAndAdd(i, 1);
    }

    /**
     * getAndAdd(-1)
     */
    public final int getAndDecrement(int i) {
        return getAndAdd(i, -1);
    }

    /**
     *  value = get(i)
     *  cas(value, value+i)
     *  return value
     */
    public final int getAndAdd(int i, int delta) {
        return unsafe.getAndAddInt(array, checkedByteOffset(i), delta);
    }

    /**
     * getAndAdd(i, 1) + 1
     */
    public final int incrementAndGet(int i) {
        return getAndAdd(i, 1) + 1;
    }

    /**
     * getAndAdd(-1) - 1
     */
    public final int decrementAndGet(int i) {
        return getAndAdd(i, -1) - 1;
    }

    /**
     * getAndAdd(i, delta) + delta
     */
    public final int addAndGet(int i, int delta) {
        return getAndAdd(i, delta) + delta;
    }


    /**
     * value = get(i)
     * update = func(value)
     * set(i, update)
     */
    public final int getAndUpdate(int i, IntUnaryOperator updateFunction) {
        long offset = checkedByteOffset(i);
        int prev, next;
        do {
            // get
            prev = getRaw(offset);
            // update
            next = updateFunction.applyAsInt(prev);
            // cas
        } while (!compareAndSetRaw(offset, prev, next));
        return prev;
    }

    /**
     * set(i, func(get(i)))
     * return get(i)
     */
    public final int updateAndGet(int i, IntUnaryOperator updateFunction) {
        long offset = checkedByteOffset(i);
        int prev, next;
        do {
            prev = getRaw(offset);
            next = updateFunction.applyAsInt(prev);
        } while (!compareAndSetRaw(offset, prev, next));
        return next;
    }

    /**
     * value = get(i)
     * set(i, func(x, value))
     */
    public final int getAndAccumulate(int i, int x,
                                      IntBinaryOperator accumulatorFunction) {
        long offset = checkedByteOffset(i);
        int prev, next;
        do {
            prev = getRaw(offset);
            next = accumulatorFunction.applyAsInt(prev, x);
        } while (!compareAndSetRaw(offset, prev, next));
        return prev;
    }

    /**
     * set(i, func(x, get(i)))
     * get(i)
     */
    public final int accumulateAndGet(int i, int x,
                                      IntBinaryOperator accumulatorFunction) {
        long offset = checkedByteOffset(i);
        int prev, next;
        do {
            prev = getRaw(offset);
            next = accumulatorFunction.applyAsInt(prev, x);
        } while (!compareAndSetRaw(offset, prev, next));
        return next;
    }

    public String toString() {
        return Arrays.toString(this.array);
    }

}
