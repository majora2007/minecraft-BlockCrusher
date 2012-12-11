/**
 * 
 */
package com.github.majora2007.blockcrusher;

/**
 * @author jvmilazz
 *
 */
public class CommandResponse
{
	private String responseMessage;
	
	
	public CommandResponse() {
		
	}
	
	public CommandResponse(String message) {
		this.responseMessage = message;
	}
	
	public void setResponseMessage(String message) {
		this.responseMessage = message;
	}
	
	public String getResponseMessage() {
		return this.responseMessage;
	}
	
}
