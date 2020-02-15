package thread_learning;

// STOPPING THREADS (STOPPING A RUNNABLE)
// the following code essentially uses a while loop to keep checking whether the thread should continue running
//  while loop checks for an "am I supposed to stop?" flag

// https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html
// synchronized methods: are "thread safe" -- prevent shared object over-writing, etc.
//  (establishes a queue for use of a synchronized method by multiple threads?)

public class test_Thread08 {
    public class MyRunnable implements Runnable {

        private boolean doStop = false;

        public synchronized void doStop() {
            this.doStop = true;
        }

        private synchronized boolean keepRunning() {
            return this.doStop == false;
        }

        @Override
        public void run() {
            while(keepRunning()) {
                // keep doing what this thread should do.
                System.out.println("Running");

                try {
                    Thread.sleep(3L * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public static void main(String[] args) {

    }
}
