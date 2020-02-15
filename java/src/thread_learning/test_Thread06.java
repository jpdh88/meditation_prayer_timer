package thread_learning;

// a bunch of programmatically-created threads
public class test_Thread06 {
    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName());
        for(int i=0; i<10; i++){
            new Thread("" + i){
                public void run(){
                    System.out.println("Thread: " + getName() + " running");
                }
            }.start();
        }
    }
}