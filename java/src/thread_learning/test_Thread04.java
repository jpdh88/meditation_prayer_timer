package thread_learning;

class MyRunnable implements Runnable {

    public void run(){
        System.out.println("MyRunnable running");
    }
}

// Run code in a thread with a Runnable Interface (METHOD 2)
public class test_Thread04 {

    public static void main(String[] args) {
        Runnable runnable = new MyRunnable(); // or an anonymous class, or lambda...

        Thread thread = new Thread(runnable);
        thread.start();
    }

}

// SUBCLASS OR RUNNABLE?? (METHOD 1 OR METHOD 2???)
/*
FROM WEBSITE:
There are no rules about which of the two methods that is the best. Both methods works.
Personally though, I prefer implementing Runnable, and handing an instance of the implementation
to a Thread instance. When having the Runnable's executed by a thread pool it is easy to queue
up the Runnable instances until a thread from the pool is idle. This is a little harder to do with
Thread subclasses.

Sometimes you may have to implement Runnable as well as subclass Thread. For instance, if creating
a subclass of Thread that can execute more than one Runnable. This is typically the case when
implementing a thread pool.
 */
