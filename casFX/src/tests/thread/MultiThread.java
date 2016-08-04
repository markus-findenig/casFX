package tests.thread;

public class MultiThread extends Thread{
    @Override
	public void run(){
        System.out.println("Running Thread Name: "+ Thread.currentThread().getName());
        System.out.println("Running Thread Priority: "+ Thread.currentThread().getPriority());
    }
}