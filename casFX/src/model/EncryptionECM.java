package model;

/**
 * 
 * Encryption ECM
 *
 */
public class EncryptionECM {
	
	/**
	 * ECM Section Header
	 */
	private String ecmHeader;

	/**
	 * ECM Protocol number. Code that serves to identify processing functions on
	 * the IC card, encryption algorithms, etc.
	 */
	private String ecmProtocol;
	
	/**
	 * ECM Broadcaster group identifier. Code used to identify broadcaster
	 * groups in conditional access system operation. Combined with the work key
	 * identifier, specifies the work.
	 */
	private String ecmBroadcastId;
	
	/**
	 * ECM Work key identifier. Specifies the work key used to encrypt ECM, is
	 * combined with the broadcaster group identifier.
	 */
	private String ecmWorkKeyId;
	
	/**
	 * ECM Control Word (CW), Scrambling key odd.
	 */
	private String ecmCwOdd;
	
	/**
	 *
	 * ECM Control Word (CW), Scrambling key even.
	 */
	private String ecmCwEven;
	
	/**
	 * ECM Program type. Indicates the viewing program type (free, tier, PPV, etc.).
	 */
	private String ecmProgramType;
	
	/**
	 * ECM Date Time. Indicates the current date/time to check authorization of viewing.
	 */
	private String ecmDateTime;
	
	/**
	 * ECM Recording control. Indicate the recording conditions for the program
	 * in question (recordable, not recordable, recordable by subscribers only,
	 * etc.).
	 */
	private String ecmRecordControl;
	
	/**
	 * ECM Payload
	 */
	private String ecmVariablePart;
	
	/**
	 * ECM Message Authentication Code (MAC, 4 Bytes).
	 */
	private String ecmMAC;
	
	/**
	 * ECM Cyclic Redundancy Check (CRC, 4 Bytes).
	 */
	private String ecmCRC;

	/**
	 * @return the ecmHeader
	 */
	public String getEcmHeader() {
		return ecmHeader;
	}

	/**
	 * @param ecmHeader the ecmHeader to set
	 */
	public void setEcmHeader(String ecmHeader) {
		this.ecmHeader = ecmHeader;
	}

	/**
	 * @return the ecmProtocol
	 */
	public String getEcmProtocol() {
		return ecmProtocol;
	}

	/**
	 * @param ecmProtocol the ecmProtocol to set
	 */
	public void setEcmProtocol(String ecmProtocol) {
		this.ecmProtocol = ecmProtocol;
	}

	/**
	 * @return the ecmBroadcastId
	 */
	public String getEcmBroadcastId() {
		return ecmBroadcastId;
	}

	/**
	 * @param ecmBroadcastId the ecmBroadcastId to set
	 */
	public void setEcmBroadcastId(String ecmBroadcastId) {
		this.ecmBroadcastId = ecmBroadcastId;
	}

	/**
	 * @return the ecmWorkKeyId
	 */
	public String getEcmWorkKeyId() {
		return ecmWorkKeyId;
	}

	/**
	 * @param ecmWorkKeyId the ecmWorkKeyId to set
	 */
	public void setEcmWorkKeyId(String ecmWorkKeyId) {
		this.ecmWorkKeyId = ecmWorkKeyId;
	}

	/**
	 * @return the ecmCwOdd
	 */
	public String getEcmCwOdd() {
		return ecmCwOdd;
	}

	/**
	 * @param ecmCwOdd the ecmCwOdd to set
	 */
	public void setEcmCwOdd(String ecmCwOdd) {
		this.ecmCwOdd = ecmCwOdd;
	}

	/**
	 * @return the ecmCwEven
	 */
	public String getEcmCwEven() {
		return ecmCwEven;
	}

	/**
	 * @param ecmCwEven the ecmCwEven to set
	 */
	public void setEcmCwEven(String ecmCwEven) {
		this.ecmCwEven = ecmCwEven;
	}

	/**
	 * @return the ecmProgramType
	 */
	public String getEcmProgramType() {
		return ecmProgramType;
	}

	/**
	 * @param ecmProgramType the ecmProgramType to set
	 */
	public void setEcmProgramType(String ecmProgramType) {
		this.ecmProgramType = ecmProgramType;
	}

	/**
	 * @return the ecmDateTime
	 */
	public String getEcmDateTime() {
		return ecmDateTime;
	}

	/**
	 * @param ecmDateTime the ecmDateTime to set
	 */
	public void setEcmDateTime(String ecmDateTime) {
		this.ecmDateTime = ecmDateTime;
	}

	/**
	 * @return the ecmRecordControl
	 */
	public String getEcmRecordControl() {
		return ecmRecordControl;
	}

	/**
	 * @param ecmRecordControl the ecmRecordControl to set
	 */
	public void setEcmRecordControl(String ecmRecordControl) {
		this.ecmRecordControl = ecmRecordControl;
	}

	/**
	 * @return the ecmVariablePart
	 */
	public String getEcmVariablePart() {
		return ecmVariablePart;
	}

	/**
	 * @param ecmVariablePart the ecmVariablePart to set
	 */
	public void setEcmVariablePart(String ecmVariablePart) {
		this.ecmVariablePart = ecmVariablePart;
	}

	/**
	 * @return the ecmMAC
	 */
	public String getEcmMAC() {
		return ecmMAC;
	}

	/**
	 * @param ecmMAC the ecmMAC to set
	 */
	public void setEcmMAC(String ecmMAC) {
		this.ecmMAC = ecmMAC;
	}

	/**
	 * @return the ecmCRC
	 */
	public String getEcmCRC() {
		return ecmCRC;
	}

	/**
	 * @param ecmCRC the ecmCRC to set
	 */
	public void setEcmCRC(String ecmCRC) {
		this.ecmCRC = ecmCRC;
	}
	
	
}
