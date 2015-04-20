package com.unico.gcdapp;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
/**
 * REST service to push operands into JMS queue
 * @author NAVEEN
 *
 */
@Path("/operand")
public class OperandCollector {
	
	@GET
	@Path("/push/{op1}-{op2}")
	@Produces(MediaType.TEXT_PLAIN)
	public String push(@PathParam("op1") int op1, @PathParam("op2") int op2) {
		try{
			int[] operands = {op1, op2};
			JMSManager.getProduceMessage(operands);
		}catch(Exception e){
			e.printStackTrace();
			return "Exception : " + e.toString();
		}
		return "Success";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public int[] list() {
		int[] retArr = null;
		try{
			List<String> msgList = JMSManager.peekIntoMessages();
			retArr = new int[msgList.size()];
			int i=0;
			for(String msg: msgList){
				retArr[i++] = Integer.parseInt(msg);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return retArr;
	}
}
