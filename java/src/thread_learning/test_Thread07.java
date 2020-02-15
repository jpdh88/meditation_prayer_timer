package thread_learning;

// delay only some things
class MyThread07 extends Thread {
    final int num;

    public MyThread07(int num) {
        this.num = num;
        this.setName("" + num);
    }

    public void run(){
        if (num % 2 == 0) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Thread: " + getName() + " running");
    }
}

// sleeping threads
public class test_Thread07 {
    public static void main(String[] args) {
        MyThread07[] threadArray = new MyThread07[10];

        for(int i=0; i<10; i++){
            threadArray[i] = new MyThread07(i);
            threadArray[i].start();
        }
    }
}