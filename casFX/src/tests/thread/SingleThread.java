package tests.thread;

public class SingleThread extends Thread{
    @Override
	public void run(){
        System.out.println("Single Thread.");
    }
}