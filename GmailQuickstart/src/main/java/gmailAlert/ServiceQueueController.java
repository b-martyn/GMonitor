/*
 * Controller class for the ServiceQueueView
 * 
 * Displays an alert JDialog when messages in the ServiceQueue model are overdue
 * 
 * @author Brian Martyn
 * Copyright 2015
 */
package gmailAlert;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

import javax.swing.Box;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JLabel;
import javax.swing.JTextField;

import static gmailAlert.Messages.*;

import javax.swing.SwingConstants;

/**
 * The Class ServiceQueueController.
 */
public class ServiceQueueController extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private ServiceQueueOverdueAlert alert = new ServiceQueueOverdueAlert(this);
	
	private JPanel contentPane;
	private ServiceQueueView view;
	private JLabel lblLastUpdate;
	private JLabel lblUnreadMessageThreshold;
	private JLabel lblWaitingMessageThreshold;
	private JTextField textFieldWaitingMinutes;
	private JTextField textFieldWaitingSeconds;
	private JTextField textFieldUnreadMinutes;
	private JTextField textFieldUnreadSeconds;

	/**
	 * Launch the application.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			ServiceQueueController frame = new ServiceQueueController();
			frame.view.setView(ServiceQueueFactory.getServiceQueueInstance());
			frame.setVisible(true);
			while(true){
				try{
					java.lang.Thread.sleep(getUpdatedDelay());
				}catch(InterruptedException e){
					break;
				}
				frame.reload();
				System.out.println("Reload");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	public ServiceQueueController() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 550);
		{
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			
			JMenu mnFile = new JMenu("File");
			menuBar.add(mnFile);
			
			JMenuItem mntmRefresh = new JMenuItem("Refresh");
			mnFile.add(mntmRefresh);
			mntmRefresh.setActionCommand("Refresh");
			
			JMenuItem mntmHide = new JMenuItem("Hide");
			mnFile.add(mntmHide);
			mntmHide.setActionCommand("Hide");
			mntmHide.addActionListener(this);
			mntmRefresh.addActionListener(this);
			
			JMenu mnThreshold = new JMenu("Threshold");
			menuBar.add(mnThreshold);
			
			{
				JMenu mnUnreadMessages = new JMenu("Unread Messages");
				mnThreshold.add(mnUnreadMessages);
				
				lblUnreadMessageThreshold = new JLabel(String.format("%d:%02d min", (getNewMessageThresholdMilliSeconds()/1000)/60, (getNewMessageThresholdMilliSeconds()/1000)%60));
				mnUnreadMessages.add(lblUnreadMessageThreshold);
				{
					JMenu muUnreadEdit = new JMenu("Edit");
					mnUnreadMessages.add(muUnreadEdit);
					
					textFieldUnreadMinutes = new JTextField(String.valueOf((int)getNewMessageThresholdMilliSeconds() / MINUTES));
					textFieldUnreadMinutes.setColumns(10);
					muUnreadEdit.add(textFieldUnreadMinutes);
					
					JLabel lblUnreadMinutes = new JLabel("Minutes");
					muUnreadEdit.add(lblUnreadMinutes);
					
					textFieldUnreadSeconds = new JTextField(String.valueOf((int)getNewMessageThresholdMilliSeconds() % SECONDS));
					textFieldUnreadSeconds.setColumns(10);
					muUnreadEdit.add(textFieldUnreadSeconds);
					
					JLabel lblUnreadSeconds = new JLabel("Seconds");
					muUnreadEdit.add(lblUnreadSeconds);
					
					JMenuItem mntmUnreadSet = new JMenuItem("Set");
					mntmUnreadSet.setActionCommand("UnreadSet");
					mntmUnreadSet.addActionListener(this);
					muUnreadEdit.add(mntmUnreadSet);
				}
			}
			
			{
				JMenu mnWaitingMessages = new JMenu("Waiting Messages");
				mnThreshold.add(mnWaitingMessages);
				
				lblWaitingMessageThreshold = new JLabel(String.format("%d:%02d min", getWaitingMessageThresholdMilliSeconds() / MINUTES, getWaitingMessageThresholdMilliSeconds() % SECONDS));
				mnWaitingMessages.add(lblWaitingMessageThreshold);
				{
					JMenu mnWaitingEdit = new JMenu("Edit");
					mnWaitingMessages.add(mnWaitingEdit);
					
					textFieldWaitingMinutes = new JTextField(String.valueOf((int)getWaitingMessageThresholdMilliSeconds() / MINUTES));
					mnWaitingEdit.add(textFieldWaitingMinutes);
					textFieldWaitingMinutes.setColumns(10);
					
					JLabel lblWaitingMinutes = new JLabel("Minutes");
					mnWaitingEdit.add(lblWaitingMinutes);
					
					textFieldWaitingSeconds = new JTextField(String.valueOf((int)getWaitingMessageThresholdMilliSeconds() % SECONDS));
					mnWaitingEdit.add(textFieldWaitingSeconds);
					textFieldWaitingSeconds.setColumns(10);
					
					JLabel lblWaitingSeconds = new JLabel("Seconds");
					mnWaitingEdit.add(lblWaitingSeconds);
					
					JMenuItem mntmWaitingSet = new JMenuItem("Set");
					mntmWaitingSet.setActionCommand("WaitingSet");
					mntmWaitingSet.addActionListener(this);
					mnWaitingEdit.add(mntmWaitingSet);
				}
			}
			// Sets next component to the far right
			menuBar.add(Box.createGlue());
			
			lblLastUpdate = new JLabel(new SimpleDateFormat("MM/dd hh:mm:ss").format(new Date()));
			lblLastUpdate.setHorizontalAlignment(SwingConstants.CENTER);
			menuBar.add(lblLastUpdate);
		}
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0};
		gbl_contentPane.rowHeights = new int[]{0};
		gbl_contentPane.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		view = new ServiceQueueView();
		GridBagConstraints gbc_view = new GridBagConstraints();
		gbc_view.fill = GridBagConstraints.BOTH;
		gbc_view.gridx = 0;
		gbc_view.gridy = 0;
		contentPane.add(view, gbc_view);
	}
	
	/**
	 * Reload with new ServiceQueue.
	 * 
	 * In the event that an authorized connection to a 
	 * G-mail account fails, an empty ServiceQueue is loaded
	 */
	public void reload() {
		ServiceQueue newQueue;
		try {
			newQueue = ServiceQueueFactory.getServiceQueueInstance();
		} catch (IOException e) {
			newQueue = new ServiceQueue();
			newQueue.setUnread(new ArrayList<Thread>());
			newQueue.setWaiting(new ArrayList<Thread>());
		}
		view.setView(newQueue);
		lblLastUpdate.setText(new SimpleDateFormat("MM/dd hh:mm:ss").format(new Date()));
		notifyOverdue(newQueue);
	}
	
	/**
	 * Generates a new ServiceQueue from provided populated with only the threads that are overdue
	 * Displays a new alert if the last one was closed and signals a beep if the alert did not have focus
	 * 
	 * @param serviceQueue
	 */
	private void notifyOverdue(ServiceQueue serviceQueue) {
		ServiceQueue overdueQueue = new ServiceQueue();
		overdueQueue.setUnread(getOverdueThreads(serviceQueue.getUnread(), Messages.Type.UNREAD));
		overdueQueue.setWaiting(getOverdueThreads(serviceQueue.getWaiting(), Messages.Type.WAITING));
		if(!overdueQueue.isEmpty()){
			if(alert.isVisible()){
				alert.setView(overdueQueue);
				if(!alert.hasFocus()){
					Toolkit.getDefaultToolkit().beep();
				}
			}else{
				alert = new ServiceQueueOverdueAlert(this, overdueQueue);
				alert.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				alert.setVisible(true);
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}
	
	/**
	 * Generates a new list of threads containing only those which are 
	 * overdue from the input thread list based on they message type
	 * 
	 * @param list of threads
	 * @param message type
	 * @return new list of overdue threads
	 */
	private List<Thread> getOverdueThreads(List<Thread> threads, Messages.Type type) {
		List<Thread> overdue = new ArrayList<>();
		
		for(Thread thread : threads){
			Message lastMessage = getLatestMessage(thread);
			if(isOverdue(lastMessage, type)){
				view.markOverdue(thread, type);
				overdue.add(thread);
			}
		}
		
		return overdue;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		switch(actionEvent.getActionCommand()){
			case "UnreadSet":
				newThreshold(Messages.Type.UNREAD, textFieldUnreadMinutes, textFieldUnreadSeconds);
				break;
			case "WaitingSet":
				newThreshold(Messages.Type.WAITING, textFieldWaitingMinutes, textFieldWaitingSeconds);
				break;
			case "Refresh":
				reload();
				break;
			case "Hide":
				setVisible(false);
				break;
			default:
				System.out.println("Missed action command: " + actionEvent.getActionCommand());
		}
	}
	
	/**
	 * Define a new threshold that signifies a type of message is late
	 * Updates static fields of Messages class
	 * 
	 * @param type message type
	 * @param minutes JTextField containing the number of minutes
	 * @param seconds JTextField containing the number of seconds
	 */
	private void newThreshold(Messages.Type type, JTextField minutes, JTextField seconds){
		// Format minutes
		int newMinutes = 0;
		try{
			newMinutes = Integer.parseInt(minutes.getText());
		}catch(NumberFormatException e){
			if(minutes.getText().isEmpty()){
				// Do nothing, keep value of seconds as 0
			}else{
				// Highlight minutes field as not a real number
				minutes.setForeground(Color.RED);
				minutes.addFocusListener(new FocusListener(){

					@Override
					public void focusGained(FocusEvent focusEvent) {
						((JTextField)focusEvent.getSource()).setForeground(Color.BLACK);
					}

					@Override
					public void focusLost(FocusEvent arg0) {
						// Do nothing
					}
					
				});
			}
		}
		// Format seconds
		int newSeconds = 0;
		try{
			newSeconds = Integer.parseInt(seconds.getText());
		}catch(NumberFormatException e){
			if(seconds.getText().isEmpty()){
				// Do nothing, keep value of seconds as 0
			}else{
				// Highlight seconds field as not a real number
				seconds.setForeground(Color.RED);
				seconds.addFocusListener(new FocusListener(){

					@Override
					public void focusGained(FocusEvent focusEvent) {
						((JTextField)focusEvent.getSource()).setForeground(Color.BLACK);
					}

					@Override
					public void focusLost(FocusEvent arg0) {
						// Do nothing
					}
					
				});
			}
		}
		
		// Validate and set or do nothing if invalid
		if(seconds.getForeground().equals(Color.RED) || minutes.getForeground().equals(Color.RED)){
			// Do nothing, invalid amount
		}else{
			int newThreshold = (newMinutes * MINUTES) + (newSeconds * SECONDS);
			switch(type){
				case UNREAD:
					Messages.setNewMessageThresholdMilliSeconds(newThreshold);
					lblUnreadMessageThreshold.setText(String.format("%d:%02d min", newMinutes, newSeconds));
					break;
				case WAITING:
					Messages.setWaitingMessageThresholdMilliSeconds(newThreshold);
					lblWaitingMessageThreshold.setText(String.format("%d:%02d min", newMinutes, newSeconds));
					break;
				default:
					break;
			}
			minutes.setText(String.valueOf(newMinutes));
			seconds.setText(String.valueOf(newSeconds));
		}
	}
}
