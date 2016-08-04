package model;

public class EncryptionECM {
	
	// ECM
	private String ecmHeader;
	private String ecmProtocol;
	private String ecmBroadcastId;
	private String ecmWorkKeyId;
	private String ecmCwOdd;
	private String ecmCwEven;
	private String ecmProgramType;
	private String ecmDateTime;
	private String ecmRecordControl;
	private String ecmVariablePart;
	private String ecmMAC;
	private String ecmCRC;
	
//	public EncryptionECM (){
//		
//	}
	
	public String getEcmHeader() {
		return ecmHeader;
	}

	public void setEcmHeader(String eHeader) {
		ecmHeader = eHeader;
	}

	public String getEcmProtocol() {
		return ecmProtocol;
	}

	public void setEcmProtocol(String ecmProtocol) {
		this.ecmProtocol = ecmProtocol;
	}

	public String getEcmBroadcastId() {
		return ecmBroadcastId;
	}

	public void setEcmBroadcastId(String ecmBroadcastId) {
		this.ecmBroadcastId = ecmBroadcastId;
	}

	public String getEcmWorkKeyId() {
		return ecmWorkKeyId;
	}

	public void setEcmWorkKeyId(String ecmWorkKeyId) {
		this.ecmWorkKeyId = ecmWorkKeyId;
	}

	public String getEcmCwOdd() {
		return ecmCwOdd;
	}

	public void setEcmCwOdd(String ecmCwOdd) {
		this.ecmCwOdd = ecmCwOdd;
	}

	public String getEcmCwEven() {
		return ecmCwEven;
	}

	public void setEcmCwEven(String ecmCwEven) {
		this.ecmCwEven = ecmCwEven;
	}

	public String getEcmProgramType() {
		return ecmProgramType;
	}

	public void setEcmProgramType(String ecmProgramType) {
		this.ecmProgramType = ecmProgramType;
	}

	public String getEcmDateTime() {
		return ecmDateTime;
	}

	public void setEcmDateTime(String ecmDateTime) {
		this.ecmDateTime = ecmDateTime;
	}

	public String getEcmRecordControl() {
		return ecmRecordControl;
	}

	public void setEcmRecordControl(String ecmRecordControl) {
		this.ecmRecordControl = ecmRecordControl;
	}

	public String getEcmVariablePart() {
		return ecmVariablePart;
	}

	public void setEcmVariablePart(String ecmVariablePart) {
		this.ecmVariablePart = ecmVariablePart;
	}

	public String getEcmMAC() {
		return ecmMAC;
	}

	public void setEcmMAC(String ecmMac) {
		this.ecmMAC = ecmMac;
	}

	public String getEcmCRC() {
		return ecmCRC;
	}

	public void setEcmCRC(String ecmCrc) {
		this.ecmCRC = ecmCrc;
	}
}
