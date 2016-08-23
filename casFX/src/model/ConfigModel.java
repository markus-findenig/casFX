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

	/**
	 * Path to FFmpeg Library
	 */
	private String ffmpegPath;

	/**
	 * Path to VLC Library
	 */
	private String vlcPath;

	/**
	 * Server Address. If Constant Control Word, Time = 0. Default is
	 * rtp://239.0.0.1:5004.
	 */
	private String server;

	/**
	 * Client Address. If Constant Control Word, Time = 0. Default is
	 * rtp://239.0.0.1:5004.
	 */
	private String client;

	/**
	 * Constant Control Word (CW). If Time = 0.
	 */
	private String constantCw;

	/**
	 * 
	 * @param dStage
	 *            the dialog Stage to set
	 */
	public ConfigModel(Stage dStage) {
		dialogStage = dStage;
	}

	/**
	 * @return the dialogStage
	 */
	public Stage getDialogStage() {
		return dialogStage;
	}

	/**
	 * @return the ffmpegPath
	 */
	public String getFFmpegPath() {
		return ffmpegPath;
	}

	/**
	 * @param ffmpegPath
	 *            the ffmpegPath to set
	 */
	public void setFFmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}

	/**
	 * @return the vlcPath
	 */
	public String getVlcPath() {
		return vlcPath;
	}

	/**
	 * @param vlcPath
	 *            the vlcPath to set
	 */
	public void setVlcPath(String vlcPath) {
		this.vlcPath = vlcPath;
	}

	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return the client
	 */
	public String getClient() {
		return client;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(String client) {
		this.client = client;
	}

	/**
	 * @return the constantCw
	 */
	public String getConstantCw() {
		return constantCw;
	}

	/**
	 * @param constantCw
	 *            the constantCw to set
	 */
	public void setConstantCw(String constantCw) {
		this.constantCw = constantCw;
	}

}
