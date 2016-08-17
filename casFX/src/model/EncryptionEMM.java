package model;

/**
 * 
 * Encryption EMM
 *
 */
public class EncryptionEMM {
	
	/**
	 * EMM Section Header
	 */
	private String emmHeader;
	
	/**
	 * EMM Smartcard ID
	 */
	private String emmSmartcardId;
	
	/**
	 * EMM Length from Protocol Number to the MAC Field
	 */
	private String emmLength;
	
	/**
	 * EMM Protocol
	 */
	private String emmProtocol;
	
	/**
	 * EMM Broadcast Group Identifier
	 */
	private String emmBroadcastId;
	
	/**
	 * EMM Update number. Number that is increased when individual information is updated.
	 */
	private String emmUpdateId;
	
	/**
	 * EMM Expiration date. Indicates when individual information expires. 
	 */
	private String emmExpirationDate;
	
	/**
	 * EMM Payload
	 */
	private String emmVariablePart;
	
	/**
	 * EMM Message Authentication Code (MAC, 4 Bytes).
	 */
	private String emmMAC;
	
	/**
	 * EMM Cyclic Redundancy Check (CRC, 4 Bytes).
	 */
	private String emmCRC;

	/**
	 * @return the emmHeader
	 */
	public String getEmmHeader() {
		return emmHeader;
	}

	/**
	 * @param emmHeader the emmHeader to set
	 */
	public void setEmmHeader(String emmHeader) {
		this.emmHeader = emmHeader;
	}

	/**
	 * @return the emmSmartcardId
	 */
	public String getEmmSmartcardId() {
		return emmSmartcardId;
	}

	/**
	 * @param emmSmartcardId the emmSmartcardId to set
	 */
	public void setEmmSmartcardId(String emmSmartcardId) {
		this.emmSmartcardId = emmSmartcardId;
	}

	/**
	 * @return the emmLength
	 */
	public String getEmmLength() {
		return emmLength;
	}

	/**
	 * @param emmLength the emmLength to set
	 */
	public void setEmmLength(String emmLength) {
		this.emmLength = emmLength;
	}

	/**
	 * @return the emmProtocol
	 */
	public String getEmmProtocol() {
		return emmProtocol;
	}

	/**
	 * @param emmProtocol the emmProtocol to set
	 */
	public void setEmmProtocol(String emmProtocol) {
		this.emmProtocol = emmProtocol;
	}

	/**
	 * @return the emmBroadcastId
	 */
	public String getEmmBroadcastId() {
		return emmBroadcastId;
	}

	/**
	 * @param emmBroadcastId the emmBroadcastId to set
	 */
	public void setEmmBroadcastId(String emmBroadcastId) {
		this.emmBroadcastId = emmBroadcastId;
	}

	/**
	 * @return the emmUpdateId
	 */
	public String getEmmUpdateId() {
		return emmUpdateId;
	}

	/**
	 * @param emmUpdateId the emmUpdateId to set
	 */
	public void setEmmUpdateId(String emmUpdateId) {
		this.emmUpdateId = emmUpdateId;
	}

	/**
	 * @return the emmExpirationDate
	 */
	public String getEmmExpirationDate() {
		return emmExpirationDate;
	}

	/**
	 * @param emmExpirationDate the emmExpirationDate to set
	 */
	public void setEmmExpirationDate(String emmExpirationDate) {
		this.emmExpirationDate = emmExpirationDate;
	}

	/**
	 * @return the emmVariablePart
	 */
	public String getEmmVariablePart() {
		return emmVariablePart;
	}

	/**
	 * @param emmVariablePart the emmVariablePart to set
	 */
	public void setEmmVariablePart(String emmVariablePart) {
		this.emmVariablePart = emmVariablePart;
	}

	/**
	 * @return the emmMAC
	 */
	public String getEmmMAC() {
		return emmMAC;
	}

	/**
	 * @param emmMAC the emmMAC to set
	 */
	public void setEmmMAC(String emmMAC) {
		this.emmMAC = emmMAC;
	}

	/**
	 * @return the emmCRC
	 */
	public String getEmmCRC() {
		return emmCRC;
	}

	/**
	 * @param emmCRC the emmCRC to set
	 */
	public void setEmmCRC(String emmCRC) {
		this.emmCRC = emmCRC;
	}
	

	
}
