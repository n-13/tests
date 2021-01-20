package threadlocal;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ThreadLocal alternative approach
 *
 * @param <T>
 */
public class MyThreadLocal<T> {
    /**
     * class holds value for one thread
     *
     * @param <V>
     */
    private static class ThreadData<V> {
        /**
         * Reference to a thread
         */
        private final WeakReference<Thread> threadReference;
        /**
         * Value storage
         */
        private final V value;

        /**
         * Constructor takes current thread reference and value
         * @param value value to hold
         */
        public ThreadData(V value) {
            this.value = value;
            this.threadReference = new WeakReference<>(Thread.currentThread());
        }

        /**
         * @return stored value
         */
        public V get() {
            return value;
        }

        /**
         * @return true if thread, from which value was set, collected by GC
         */
        public boolean threadIsDead(){
            return threadReference.get()==null;
        }
    }

    private final ConcurrentHashMap<Long, ThreadData<T>> threadValues = new ConcurrentHashMap<>();

    /**
     * @return value for current thread
     */
    public T get() {
        ThreadData<T> data = threadValues.get(Thread.currentThread().getId());
        return data == null ? null : data.get();
    }

    /**
     * Set value for current thread
     * @param value
     */
    public void set(T value) {
        //Add or replace a value
        threadValues.put(Thread.currentThread().getId(), new ThreadData<>(value));
        //Remove values from hashmap for dead threads
        List<Long> toRemove=new LinkedList<>();
        threadValues.forEach((threadId, data) -> {
            if(data.threadIsDead())toRemove.add(threadId);
        });
        toRemove.forEach(id -> threadValues.remove(id));
    }

}
