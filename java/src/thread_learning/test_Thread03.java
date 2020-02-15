package thread_learning;

// anonymous sub class
public class test_Thread03 {

    public static void main(String[] args) {

        Thread thread = new Thread() {
            public void run( ){
                System.out.println("Thread Running");
            }
        };

        thread.start();
    }
}
