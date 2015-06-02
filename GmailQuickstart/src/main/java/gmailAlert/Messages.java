/*
 * Utility class providing constants and methods for handling 
 * connections to GMail service via the ServiceQueueFactory
 * 
 * @author Brian Martyn
 * Copyright 2015
 */
package gmailAlert;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Thread;

/**
 * The Class Messages.
 */
public class Messages {
	
	/** The Constant DATE_FORMATTER. */
	public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("E, d MMM yyyy hh:mm:ss Z");
	
	/** The Constant DATE_HEADER. */
	public static final String DATE_HEADER = "Date";
	
	/** The Constant FROM_HEADER. */
	public static final String FROM_HEADER = "From";
	
	/** The Constant TO_HEADER. */
	public static final String TO_HEADER = "To";
	
	/** The Constant SUBJECT_HEADER. */
	public static final String SUBJECT_HEADER = "Subject";
	
	/** The Constant SECONDS. */
	public static final int SECONDS = 1000;
	
	/** The Constant MINUTES. */
	public static final int MINUTES = 60 * SECONDS;
	
	private static int updatedDelay = 10 * SECONDS;
	private static int newMessageThresholdMilliSeconds = 2 * MINUTES;
	private static int waitingMessageThresholdMilliSeconds = 20 * MINUTES;
	
	/**
	 * Enum specifying the type of message.
	 */
	public enum Type{
		
		/** Unread message. */
		UNREAD, 
		/** Message waiting for reply. */
		WAITING;
	}
	
	/**
	 * Gets the latest message.
	 *
	 * @param thread a gmail.thread
	 * @return the latest message
	 */
	public static Message getLatestMessage(Thread thread){
		return thread.getMessages().get(thread.getMessages().size() - 1);
	}
	
	/**
	 * Gets the date message was received.
	 *
	 * @param message the message
	 * @return the date message was received
	 */
	public static Date getReceivedDate(Message message) {
		for(MessagePartHeader header : message.getPayload().getHeaders()){
			if(header.getName().equals(DATE_HEADER)){
					try{
						return DATE_FORMATTER.parse(header.getValue());
					}catch (ParseException e){
						try{
							message = ServiceQueueFactory.getFullMessage(message.getId());
							return getReceivedDate(message);
						}catch (IOException e2){
							// Error with connection
							return new Date();
						}
					}
			}
		}
		// Should never reach here
		return null;
	}
	
	/**
	 * Gets the message sender.
	 *
	 * @param message the full message.
	 * @return the message sender.  if message is not full will return null
	 */
	public static String getMessageSender(Message message) {
		for(MessagePartHeader header : message.getPayload().getHeaders()){
			if(header.getName().equals(FROM_HEADER)){
				return header.getValue();
			}
		}
		// Not a fully loaded message
		return null;
	}
	
	/**
	 * Gets the receiver of the message
	 * 
	 * @param message the full message
	 * @return the message receiver.  if message is not full will return null
	 */
	public static String getMessageReceiver(Message message){
		for(MessagePartHeader header : message.getPayload().getHeaders()){
			if(header.getName().equals(TO_HEADER)){
				String value = header.getValue();
				return value.substring((value.indexOf('<')) + 1, value.length() - 1);
			}
		}
		// Not a fully loaded message
		return null;
	}
	
	/**
	 * Checks if is overdue.
	 *
	 * @param message the message
	 * @param type the type of message
	 * @return true, if message is overdue
	 */
	public static boolean isOverdue(Message message, Type type) {
		boolean result = false;
		
		Date received = Messages.getReceivedDate(message);
		// Get proper threshold
		int threshold;
		switch(type){
			case UNREAD:
				threshold = newMessageThresholdMilliSeconds;
				break;
			case WAITING:
				threshold = waitingMessageThresholdMilliSeconds;
				break;
			default:
				threshold = 0;
				break;
		}
		// Do calculation for overdue
		if((received.getTime() + (threshold)) < new Date().getTime()){
			result = true;
		}
		
		return result;
	}
	
	public static int getUpdatedDelay() {
		return updatedDelay;
	}

	public static void setUpdatedDelay(int delay) {
		updatedDelay = delay;
	}

	public static int getNewMessageThresholdMilliSeconds() {
		return newMessageThresholdMilliSeconds;
	}

	public static void setNewMessageThresholdMilliSeconds(int milliseconds) {
		newMessageThresholdMilliSeconds = milliseconds;
	}

	public static int getWaitingMessageThresholdMilliSeconds() {
		return waitingMessageThresholdMilliSeconds;
	}

	public static void setWaitingMessageThresholdMilliSeconds(int milliseconds) {
		waitingMessageThresholdMilliSeconds = milliseconds;
	}

	/**
	 * The Class ThreadComparator.
	 * 
	 * Sorts threads from earliest to latest received date
	 */
	public static class ThreadComparator implements Comparator<Thread>{
		
		/**
		 * Returns > 0 if thread1 received date is before thread2 received date.
		 *
		 * @param thread1 first gmail.thread
		 * @param thread2 second gmail.thread
		 * @return > 0 if thread1 was received before thread2
		 */
		@Override
		public int compare(Thread thread1, Thread thread2) {
			return getReceivedDate(getLatestMessage(thread2)).compareTo(getReceivedDate(getLatestMessage(thread1)));
		}
	}
}
