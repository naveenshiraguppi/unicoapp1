package com.unico.gcdapp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.soap.SOAPException;
import javax.xml.ws.WebServiceContext;
/**
 * SOAP service to get gcd of operands from JMS
 * @author NAVEEN
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT,use= SOAPBinding.Use.LITERAL)
public class GcdService{
    @WebMethod
	public int gcd() throws SOAPException {
    	try{
    		String[] op = JMSManager.getMessages(2);
    		if(op == null || op.length != 2 || op[0] == null || op[1] == null){
    			throw new Exception("No sufficient operands in the queue..");
    		}
			int gcd = getGcd(Integer.parseInt(op[0]), Integer.parseInt(op[1]));
			return gcd;
		}catch(Exception e){
			throw new SOAPException(e);
		}
	}

	static int getGcd(int m, int n) {
		if (m <= n && n % m == 0)
			return m;
		if (n < m)
			return getGcd(n, m);
		else
			return getGcd(m, n % m);
	}
	@WebMethod
	public List<Integer> gcdList() {
		List<Integer> retList = null;
		try{
			List<String> msgList = JMSManager.peekIntoMessages();
			System.out.println("gcdList() : msgList : " + msgList);
			if(msgList != null && msgList.size() >= 2){
				retList = new ArrayList<Integer>(msgList.size()/2);
				int loopCount = msgList.size()/2;
				for(int i=0;i<loopCount;i++){
					retList.add(getGcd(Integer.parseInt(msgList.get(i*2)), Integer.parseInt(msgList.get(i*2+1))));
				}
			}
			else
			{
				retList = new ArrayList<Integer>();
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		System.out.println("gcdList() : retList : " + retList);
		return retList;
	}
	
    @WebMethod
	public int gcdSum() {
    	List<Integer> retList = gcdList();
    	int sum = 0;
    	for(int gcd: retList)
    		sum+=gcd;
		return sum;
	}
    
}
//	wsgen -keep -cp . com.unico.gcdapp.GcdService