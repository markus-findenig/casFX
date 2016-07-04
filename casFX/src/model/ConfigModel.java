package model;

import javafx.stage.Stage;

public class ConfigModel {

	private Stage dialogStage;
	
	private String server;
	private String client;
	
	
	public ConfigModel(Stage dialogStage) {
		this.dialogStage  = dialogStage;
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
	
	

}
