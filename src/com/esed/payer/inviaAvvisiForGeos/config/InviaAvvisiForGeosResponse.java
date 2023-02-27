/**
 * 
 */
package com.esed.payer.inviaAvvisiForGeos.config;

public class InviaAvvisiForGeosResponse {

	private String code;
	private String message;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public InviaAvvisiForGeosResponse() {}
	public InviaAvvisiForGeosResponse(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	public String toString() {
		return "InvioFlussiForGEOSResponse [code="+code+
		" ,message="+message+"]";
	}



}
