package concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicBoolean implements java.io.Serializable {
    private static final long serialVersionUID = 4654671469794556979L;
    // 一些不安全的操作
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    // 一个对象指定属性的偏移值
    private static final long valueOffset;

    static {
        try {
            // 获取一个指定对象的属性的偏移值
            valueOffset = unsafe.objectFieldOffset
                (AtomicBoolean.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }
    // 主要的信息标志
    /**
     * 内部的值实际上是
     * 1 : true
     * 0 : false
     */
    private volatile int value;

    /**
     * 初始化
     */
    public AtomicBoolean(boolean initialValue) {
        value = initialValue ? 1 : 0;
    }
    // 无参构造，默认为 0: false
    public AtomicBoolean() {
    }

    /**
     * 数字转真值
     * 1 : true
     * 0 : false
     *
     * return
     *  value == 1
     *  value != 0
     */
    public final boolean get() {
        return value != 0;
    }

    /**
     * 指定状态则设置
     */
    public final boolean compareAndSet(boolean expect, boolean update) {
        // 真值转数值
        int e = expect ? 1 : 0;
        // 真值转数值
        int u = update ? 1 : 0;
        // 底层的需要知道偏移
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    /**
     * 同上
     */
    public boolean weakCompareAndSet(boolean expect, boolean update) {
        int e = expect ? 1 : 0;
        int u = update ? 1 : 0;
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    /**
     * 直接设置
     */
    public final void set(boolean newValue) {
        value = newValue ? 1 : 0;
    }

    /**
     * 懒设置
     */
    public final void lazySet(boolean newValue) {
        int v = newValue ? 1 : 0;
        unsafe.putOrderedInt(this, valueOffset, v);
    }

    /**
     * 获取之后进行设置
     */
    public final boolean getAndSet(boolean newValue) {
        boolean prev;
        do {
            // 首先得获取原来的值
            prev = get();
            // 确定是"上一次",才进行设置
        } while (!compareAndSet(prev, newValue));
        return prev;
    }

    /**
     * 打印真值
     */
    public String toString() {
        return Boolean.toString(get());
    }

}
