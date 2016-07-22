package model;

import javafx.stage.Stage;

/**
 * Model für das Config Popup
 */
public class ConfigModel {

	/**
	 * Config Model Stage
	 */
	private Stage dialogStage;
	
	private String ffmpegPath;
	private String vlcPath;
	
	private String server;
	private String client;
	
	
	public ConfigModel(Stage dStage) {
		dialogStage  = dStage;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}
	
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}

	public String getVlcPath() {
		return vlcPath;
	}

	public void setVlcPath(String vlcPath) {
		this.vlcPath = vlcPath;
	}

	public String getFfmpegPath() {
		return ffmpegPath;
	}

	public void setFfmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}
	
	

}
