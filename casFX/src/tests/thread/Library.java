package tests.thread;

/**
 * Created by fahimeh.miri on 3/11/2015.
 */
class Library extends Thread{
    Output output;

    public Library(Output output) {
        this.output = output;
    }

    @Override
	public void run(){
        output.print("Library Print.");
        output.scan("Library Print.");
    }
}
