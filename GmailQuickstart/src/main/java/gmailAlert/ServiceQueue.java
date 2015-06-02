/*
 * Object Model containing two lists of gmail.threads.
 * 
 * One list contains a set of unread threads
 * Another list contains a set of threads that have been read but are waiting for a reply
 * 
 * @author Brian Martyn
 * Copyright 2015
 */
package gmailAlert;

import java.util.List;

import com.google.api.services.gmail.model.Thread;

/**
 * The Class ServiceQueue.
 */
public class ServiceQueue {
	private List<Thread> unread;
	private List<Thread> waiting;
	
	public List<Thread> getUnread() {
		return unread;
	}
	
	public void setUnread(List<Thread> unread) {
		this.unread = unread;
	}

	public List<Thread> getWaiting() {
		return waiting;
	}

	public void setWaiting(List<Thread> waiting) {
		this.waiting = waiting;
	}
	
	public boolean isEmpty(){
		return unread.size() == 0? waiting.size() == 0 : false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServiceQueue [unread=" + unread + ", waiting=" + waiting.size() + "]";
	}
}
