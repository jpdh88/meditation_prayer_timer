import java.util.Timer;
import java.util.TimerTask;

class MyTimerTask1 extends TimerTask {
    int count = 0;

    public void run() {
        System.out.println("This");
    }
}

public class TimerTechnique4 {

    public static void main(String[] args) {
        MyTimerTask1 task = new MyTimerTask1();
        MyTimerTask1 task2 = new MyTimerTask1();
        Timer timer = new Timer();
        timer.schedule(task, 2000);
        timer.schedule(task2, 2000);

    }
}
