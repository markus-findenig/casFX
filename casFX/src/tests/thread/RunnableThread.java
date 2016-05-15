package tests.thread;

public class RunnableThread implements Runnable{

    @Override
    public void run() {
        try {
            System.out.println("Runnable Thread:"+Thread.currentThread().getName());
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
