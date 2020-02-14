import java.util.Timer;
import java.util.TimerTask;

class MyTimerTask extends TimerTask {
    int count = 0;

    public void run() {
        if (count == 10) {
            this.cancel();
        }
        System.out.println("count = " + count);
        count++;
    }
}

public class test_Timer_UsingTimerObject_sub1 {

    public static void main(String[] args) {
        MyTimerTask task = new MyTimerTask();
        Timer timer = new Timer();
        timer.schedule(task, 500, 500);
    }
}
