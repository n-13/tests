package threadlocal;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MyThreadLocalTest {
    /** Original ThreadLocal value */
    static ThreadLocal<String> localTag=new ThreadLocal<>();
    /** My implementation of ThreadLocal value */
    static MyThreadLocal<String> myLocalTag=new MyThreadLocal<>();
    /** Global value for all threads */
    static AtomicReference<String> notLocalTag=new AtomicReference<>();

    /** All started threads list */
    static List<Thread> threads=new LinkedList<>();

    private static class MyRunnable implements Runnable{
        private final String threadTag;

        public MyRunnable(String threadTag){
            this.threadTag = threadTag;
        }

        /** Show message with thread info */
        private void log(String text){
            System.out.println("Thread "+Thread.currentThread().getId()+"."+ threadTag +": "+text);
            System.out.flush();
        }

        /** Block thread for specified amount of time */
        private void sleep(int ms){
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                log(e.getMessage());
            }
        }

        /**
         * Thread procedure: <br>
         *     1. save passed argument to all 3 types of storage <br>
         *     2. sleep <br>
         *     3. show stored values
         */
        @Override
        public void run() {
            localTag.set(threadTag);
            notLocalTag.set(threadTag);
            myLocalTag.set(threadTag);
            sleep(250);
            log(
                    "  localTag="+localTag.get()+
                    "  myLocalTag="+myLocalTag.get()+
                    "  notLocalTag="+notLocalTag.get()
            );
        }
    }

    /**
     * Create thread, marked with tag
     * @param threadTag
     */
    static void addThread(String threadTag){
        Thread newThread = new Thread(
                new MyRunnable(threadTag)
        );
        threads.add(newThread);
    }
    /** Start all threads with delay */
    static void allThreadsStart(){
        threads.forEach(thread -> {
            thread.start();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });
    }
    /** Wait all threads finished */
    static void allThreadsJoin(){
        for (Thread thread : threads) {
            if(thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    public static void main(String[] args) {
        for (int i = 0; i < 9; i++) {
            addThread("T"+i);
        }
        allThreadsStart();
        allThreadsJoin();
    }
}
