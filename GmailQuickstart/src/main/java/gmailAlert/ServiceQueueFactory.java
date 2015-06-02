/*
 * Factory class that contains a singleton Gmail service instance
 * used for getting ServiceQueue's and full Threads and Messages
 * 
 * @author Brian Martyn
 * Copyright 2015
 */
package gmailAlert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

/**
 * A factory for creating ServiceQueue Thread and Message objects from a G-mail service
 */
public class ServiceQueueFactory {
	
	private static final String USER_ID = "me";
	private static final String UNREAD_THREADS = "Label:INBOX Label:UNREAD";
	private static final String WAITING_THREADS = "Label:INBOX -Label:UNREAD";
	private static Gmail service;
	private static String email;
	
	public static ServiceQueue getServiceQueueInstance() throws IOException{
		ServiceQueue queue = new ServiceQueue();
		initiliaze();
		
        queue.setUnread(filterUnreadThreads());
        queue.setWaiting(filterWaitingThreads());
        
		return queue;
	}
	
	private static synchronized void initiliaze() throws IOException{
		if(service == null){
			service = GmailFactory.getGmailService();
			email = service.users().getProfile(USER_ID).execute().getEmailAddress();
		}
	}
	
	/**
	 * Gets the full thread.
	 *
	 * @param id the id of the thread
	 * @return the full thread
	 * @throws IOException Signals that connection is unauthorized
	 */
	public static Thread getFullThread(String id) throws IOException{
		initiliaze();
		
		return service.users().threads().get(USER_ID, id).setFormat("full").execute();
	}
	
	/**
	 * Gets the full message.
	 *
	 * @param id the id of the message
	 * @return the full message
	 * @throws IOException Signals that connection is unauthorized
	 */
	public static Message getFullMessage(String id) throws IOException{
		initiliaze();
		
		return service.users().messages().get(USER_ID, id).setFormat("full").execute();
	}
	
	/**
	 * 
	 * @return list of threads that are unread
	 * @throws IOException Signals that connection is unauthorized
	 */
	private static List<Thread> filterUnreadThreads() throws IOException{
		ListThreadsResponse unreadResponse = service.users().threads().list(USER_ID).setQ(UNREAD_THREADS).execute();
		List<Thread> threads = new ArrayList<>();
		for(Thread thread : unreadResponse.getThreads()){
			threads.add(service.users().threads().get(USER_ID, thread.getId()).setFormat("full").execute());
		}
		return threads;
	}
	
	/**
	 * 
	 * @return list of threads that have been read but are awaiting replay from current user
	 * @throws IOException Signals that connection is unauthorized
	 */
	private static List<Thread> filterWaitingThreads() throws IOException{
		// filter out unread threads in INBOX
		ListThreadsResponse waitingResponse = service.users().threads().list(USER_ID).setQ(WAITING_THREADS).execute();
		// remove all threads that end with a reply from USER_ID
		List<Thread> threads = new ArrayList<>();
		for(Thread t : waitingResponse.getThreads()){
			Thread thread = service.users().threads().get(USER_ID, t.getId()).setFormat("full").execute();
			Message lastMessage = Messages.getLatestMessage(thread);
			if(Messages.getMessageReceiver(lastMessage).equals(email) && !lastMessage.getLabelIds().contains("UNREAD")){
				threads.add(thread);
			}
		}
		return threads;
	}
}
