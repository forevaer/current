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

public class AtomicMarkableReference<V> {
    /**
     * 内部对象
     */
    private static class Pair<T> {
        // 引用
        final T reference;
        // 标记
        final boolean mark;
        private Pair(T reference, boolean mark) {
            this.reference = reference;
            this.mark = mark;
        }
        static <T> Pair<T> of(T reference, boolean mark) {
            return new Pair<T>(reference, mark);
        }
    }

    private volatile Pair<V> pair;

    /**
     *  constructor
     */
    public AtomicMarkableReference(V initialRef, boolean initialMark) {
        pair = Pair.of(initialRef, initialMark);
    }

    /**
     * get ref
     */
    public V getReference() {
        return pair.reference;
    }

    /**
     * return mark
     */
    public boolean isMarked() {
        return pair.mark;
    }

    /**
     * markHolder[0] = mark
     * return ref
     */
    public V get(boolean[] markHolder) {
        Pair<V> pair = this.pair;
        markHolder[0] = pair.mark;
        return pair.reference;
    }

    /**
     * Pair_CAS
     */
    public boolean weakCompareAndSet(V       expectedReference,
                                     V       newReference,
                                     boolean expectedMark,
                                     boolean newMark) {
        return compareAndSet(expectedReference, newReference,
                             expectedMark, newMark);
    }

    /**
     * base_CAS ==> Object_CAS
     */
    public boolean compareAndSet(V       expectedReference,
                                 V       newReference,
                                 boolean expectedMark,
                                 boolean newMark) {
        Pair<V> current = pair;
        return
                // ref req
            expectedReference == current.reference &&
                    // mark req
            expectedMark == current.mark &&
            (       // ref req
                    (newReference == current.reference &&
                    // mark req
                    newMark == current.mark) ||
                    //  cas
                    casPair(current, Pair.of(newReference, newMark))
            );
    }

    /**
     * Pair_set
     */
    public void set(V newReference, boolean newMark) {
        Pair<V> current = pair;
        if (newReference != current.reference || newMark != current.mark)
            this.pair = Pair.of(newReference, newMark);
    }

    /**
     * same ref and change the mark
     */
    public boolean attemptMark(V expectedReference, boolean newMark) {
        Pair<V> current = pair;
        return
            expectedReference == current.reference &&
            (newMark == current.mark ||
             casPair(current, Pair.of(expectedReference, newMark)));
    }

    // unsafe
    private static final sun.misc.Unsafe UNSAFE = sun.misc.Unsafe.getUnsafe();
    // offset
    private static final long pairOffset =
        objectFieldOffset(UNSAFE, "pair", AtomicMarkableReference.class);
    // cas(obj, field1, field2)
    private boolean casPair(Pair<V> cmp, Pair<V> val) {
        return UNSAFE.compareAndSwapObject(this, pairOffset, cmp, val);
    }

    /**
     * field offset
     */
    static long objectFieldOffset(sun.misc.Unsafe UNSAFE,
                                  String field, Class<?> klazz) {
        try {
            return UNSAFE.objectFieldOffset(klazz.getDeclaredField(field));
        } catch (NoSuchFieldException e) {
            // Convert Exception to corresponding Error
            NoSuchFieldError error = new NoSuchFieldError(field);
            error.initCause(e);
            throw error;
        }
    }
}
