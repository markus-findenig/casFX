package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.media.MediaPlayer.Status;
import model.SimulatorModel;
import view.InputView;

public class CasEventHandler implements EventHandler<ActionEvent> {


	// Model
	private SimulatorModel model;

	// View
	private static InputView view;
	
	@Override
	public void handle(ActionEvent event) {
		Status status = view.mediaPlayerInput.getStatus();

		// Update CW
		if (event.getSource() == Status.PLAYING) {

			Task<Void> taskSetCW = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							while (status == Status.PLAYING) {
								// set cw
								// view.getCwTF().setText(getRandomHex(16));
								updateMessage(InputViewController.getRandomHex(16));
								int waitTime = Integer.parseInt(view.getCwTimeTF().toString());

								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});
					return null;
				}
			};
			// start the task
			taskSetCW.messageProperty()
					.addListener((obs, oldMessage, newMessage) -> view.getCwTF().setText(newMessage));
			// start the background task
			new Thread(taskSetCW).start();
		}

	}
}