package thread_learning;

// RUNNING CODE IN A THREAD (2 METHODS)

// Run code w/ a SubClass of Thread (METHOD 1)
class MyThread extends Thread {

    public void run(){
        System.out.println("MyThread running");
    }
}

public class test_Thread02 {
    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.start();
    }

}
