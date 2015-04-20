package com.unico.gcdapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * The JMS Manager to abstract all the intricate details of JMS messaging.
 * @author NAVEEN
 *
 */
public class JMSManager {
	private ActiveMQConnectionFactory cfact;
	private static JMSManager singleton;
	private static String jmsConnectionUrl="tcp://computer-0e6a45:61616";
	private JMSManager() throws IOException {
		try{
			String tempUrl = AppProperties.getApplicationProperty("JMS_CONN_URL");
			if(tempUrl != null){
				jmsConnectionUrl = tempUrl;
				System.out.println("Reading from property file: " + jmsConnectionUrl);
			}
			System.out.println("jmsConnectionUrl: " + jmsConnectionUrl);
			cfact = new ActiveMQConnectionFactory(jmsConnectionUrl);
			System.out.println("Acive MQ Connectino Factory : " + cfact);
		}catch(Exception e){
			System.out.println("Exception while creating Acive MQ Connectino Factory ");
			e.printStackTrace();
			throw e;
		}
	}

	public static JMSManager getJMSManager() throws IOException {
		if (singleton == null) {
			synchronized (JMSManager.class) {
				if (singleton == null)// double condition check for synchronized connection factory.
					singleton = new JMSManager();
			}
		}
		return singleton;
	}

	public static String[] getMessages(int count) throws JMSException, IOException {
		JMSManager confact = getJMSManager();
		if(confact == null) 
			return null;
		String [] retVal = new String[count];
		try (MOMConnection con = confact.new MOMConnection();){
			if(con != null){
				try(MOMConnection.MOMSession momSession = con.new MOMSession();){
					if(momSession != null){
						retVal = momSession.receiveMessages(count);
					}
				}
			}
		}
		return retVal;
	}

	public static List<String> peekIntoMessages() throws JMSException, IOException {
		JMSManager confact = getJMSManager();
		List<String> retVal = null;
		if(confact == null) 
			return null;
		try (MOMConnection con = confact.new MOMConnection();){
			if(con != null){
				try(MOMConnection.MOMSession momSession = con.new MOMSession();) {
					if(momSession != null){
						retVal = momSession.peekIntoMessages();
					}
				}
			}
		}
		return retVal;
	}

	public static void getProduceMessage(int[] operands) throws JMSException, IOException {
		JMSManager confact = getJMSManager();
		String retVal = null;
		if(confact == null) 
			return;
		try (MOMConnection con = confact.new MOMConnection();){
			if(con != null){
				try(MOMConnection.MOMSession momSession = con.new MOMSession();) {
					if(momSession != null){
						for(int i: operands)
							momSession.sendMessage(i);
					}
				}
			}
		}
	}
	public class MOMConnection implements AutoCloseable {
		Connection con;

		public MOMConnection() {
			try {
				con = cfact.createConnection();
				if (con != null)
					con.start();
			} catch (JMSException e) {
				System.out.println("Exception while fetching JMS Connection");
				e.printStackTrace();
			}
		}

		public void close() {
			if (con != null) {
				try {
					con.close();
				} catch (JMSException e) {
					System.out
							.println("Exception while closing JMS Connection");
					e.printStackTrace();
				}
			}
		}

		Connection getConnection() {
			return con;
		}

		public class MOMSession implements AutoCloseable {

			Session session;

			public MOMSession() {
				if (con != null) {
					try {
						session = con.createSession(false,
								Session.AUTO_ACKNOWLEDGE);
					} catch (JMSException e) {
						System.out
								.println("Exception while creating JMS session");
						e.printStackTrace();
					}
				}
			}

			public void close() {
				if (session != null) {
					try {
						session.close();
					} catch (JMSException e) {
						System.out
								.println("Exception while closing JMS session");
						e.printStackTrace();
					}
				}
			}

			public Session getSession() {
				return session;
			}

			public void sendMessage(int operand) throws JMSException{
				Destination dest1;
				dest1 = session.createQueue("GCDWORLD.TESTQ");
				MessageProducer producer = session.createProducer(dest1);
				TextMessage textMsg = session.createTextMessage(String.valueOf(operand));
				producer.send(textMsg);
				producer.close();
			}
			public String receiveMessage() throws JMSException {
				Destination dest1;
				String retVal = null;
				dest1 = session.createQueue("GCDWORLD.TESTQ");
				MessageConsumer consumer = session.createConsumer(dest1);
				// Receive with wait
				TextMessage message = (TextMessage) consumer.receive(1000);
				consumer.close();
				System.out.println("Received message is: " + (message != null?message.getText():null));
				return retVal;
			}
			public String[] receiveMessages(int count) throws JMSException {
				Destination dest1;
				String[] retVal = new String[count];
				dest1 = session.createQueue("GCDWORLD.TESTQ");
				MessageConsumer consumer = session.createConsumer(dest1);
				// Receive with wait
				for(int i=0;i<count;i++){
					TextMessage message = (TextMessage) consumer.receive(1000);
					if(message != null){
						retVal[i]=message.getText();
						System.out.println("receiveMessages : " + retVal[i]);
					}
				}
				consumer.close();
				return retVal;
			}


			public List<String> peekIntoMessages() throws JMSException {
				Queue dest1;
				List<String> messageList = new ArrayList<String>(20);
				dest1 = session.createQueue("GCDWORLD.TESTQ");
				QueueBrowser consumer = session.createBrowser(dest1);
				// Receive with wait
				TextMessage message = null;
				Enumeration messages = consumer.getEnumeration();
				while (messages.hasMoreElements()) {
					message = (TextMessage) messages.nextElement();
					messageList.add(message.getText());
				}
				System.out.println("Received messageList is: " + (messageList));
				return messageList;
			}
		}
	}
}
