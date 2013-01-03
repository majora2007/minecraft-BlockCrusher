/**
 * Distributed under the The Non-Profit Open Software License version 3.0 (NPOSL-3.0)
 * http://www.opensource.org/licenses/NOSL3.0
 */
package com.github.majora2007.blockcrusher;

import org.bukkit.command.CommandSender;

/**
 * @author jvmilazz
 *
 */
public class CommandResponse
{
	private String responseMessage;
	private CommandSender respondant;
	
	
	
	public CommandResponse() {
		responseMessage = "";
		respondant = null;
	}
	
	public CommandResponse(String message) {
		this.responseMessage = message;
	}
	
	public CommandResponse(CommandSender respondant, String message) {
		this.respondant = respondant;
		this.responseMessage = message;
	}
	
	public void setResponseMessage(String message) {
		this.responseMessage = message;
	}
	
	public String getResponseMessage() {
		return this.responseMessage;
	}
	
	public void setRespondant(CommandSender respondant) {
		this.respondant = respondant;
	}
	
	public CommandSender getRespondant() {
		return this.respondant;
	}
	
}
