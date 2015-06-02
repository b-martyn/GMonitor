/*
 * Split pane window displaying each list in a 
 * ServiceQueue model as a JList wrapped in a JScrollPane
 * 
 * @author Brian Martyn
 * Copyright 2015
 */
package gmailAlert;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import com.google.api.services.gmail.model.Thread;

import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

/**
 * The Class ServiceQueueView.
 */
public class ServiceQueueView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	//private ServiceQueue serviceQueue;
	private JList<Thread> listNewMessages;
	private JList<Thread> listWaitingMessages;
	
	/**
	 * Instantiates a new service queue view.
	 */
	public ServiceQueueView() {
		initialize();
	}
	
	/**
	 * Instantiates a new service queue view.
	 *
	 * @param serviceQueue the service queue
	 */
	public ServiceQueueView(ServiceQueue serviceQueue){
		this();
		setView(serviceQueue);
	}
	
	private void initialize(){
		setPreferredSize(new Dimension(250, 800));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JSplitPane serviceQueueView = new JSplitPane();
		serviceQueueView.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_serviceQueueView = new GridBagConstraints();
		gbc_serviceQueueView.fill = GridBagConstraints.BOTH;
		gbc_serviceQueueView.gridx = 0;
		gbc_serviceQueueView.gridy = 0;
		add(serviceQueueView, gbc_serviceQueueView);
		
		{
			JPanel unreadMessagePanel = new JPanel();
			GridBagLayout gbl_unreadMessagePanel = new GridBagLayout();
			gbl_unreadMessagePanel.columnWidths = new int[]{0, 0};
			gbl_unreadMessagePanel.rowHeights = new int[]{0, 0, 0};
			gbl_unreadMessagePanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gbl_unreadMessagePanel.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
			unreadMessagePanel.setLayout(gbl_unreadMessagePanel);
			{
				
				JScrollPane scrollPaneNewMessages = new JScrollPane();
				scrollPaneNewMessages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				{
					JPanel newMessageListPanel = new JPanel();
					GridBagLayout gbl_newMessageListPanel = new GridBagLayout();
					gbl_newMessageListPanel.columnWidths = new int[]{0, 0};
					gbl_newMessageListPanel.rowHeights = new int[]{0, 0};
					gbl_newMessageListPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
					gbl_newMessageListPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
					newMessageListPanel.setLayout(gbl_newMessageListPanel);
					
					listNewMessages = new JList<Thread>();
					listNewMessages.setEnabled(false);
					listNewMessages.setCellRenderer(new MessageView());
					GridBagConstraints gbc_listNewMessages = new GridBagConstraints();
					gbc_listNewMessages.fill = GridBagConstraints.BOTH;
					gbc_listNewMessages.gridx = 0;
					gbc_listNewMessages.gridy = 0;
					newMessageListPanel.add(listNewMessages, gbc_listNewMessages);
					
					scrollPaneNewMessages.setViewportView(newMessageListPanel);
				}
				JLabel unreadLabel = new JLabel("Pending Unread Messages");
				unreadLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
				GridBagConstraints gbc_unreadLabel = new GridBagConstraints();
				gbc_unreadLabel.fill = GridBagConstraints.VERTICAL;
				gbc_unreadLabel.gridx = 0;
				gbc_unreadLabel.gridy = 0;
				unreadMessagePanel.add(unreadLabel, gbc_unreadLabel);
				
				GridBagConstraints gbc_scrollPaneNewMessages = new GridBagConstraints();
				gbc_scrollPaneNewMessages.fill = GridBagConstraints.HORIZONTAL;
				gbc_scrollPaneNewMessages.gridx = 0;
				gbc_scrollPaneNewMessages.gridy = 1;
				unreadMessagePanel.add(scrollPaneNewMessages, gbc_scrollPaneNewMessages);
			}
			serviceQueueView.setLeftComponent(unreadMessagePanel);
		}
		{
			JPanel waitingMessagePanel = new JPanel();
			GridBagLayout gbl_waitingMessagePanel = new GridBagLayout();
			gbl_waitingMessagePanel.columnWidths = new int[]{0, 0};
			gbl_waitingMessagePanel.rowHeights = new int[]{0, 0, 0};
			gbl_waitingMessagePanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gbl_waitingMessagePanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			waitingMessagePanel.setLayout(gbl_waitingMessagePanel);
			{
				
				JScrollPane scrollPaneWaitingMessages = new JScrollPane();
				scrollPaneWaitingMessages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				{
					JPanel waitingMessageListPanel = new JPanel();
					GridBagLayout gbl_waitingMessageListPanel = new GridBagLayout();
					gbl_waitingMessageListPanel.columnWidths = new int[]{0, 0};
					gbl_waitingMessageListPanel.rowHeights = new int[]{0, 0};
					gbl_waitingMessageListPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
					gbl_waitingMessageListPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
					waitingMessageListPanel.setLayout(gbl_waitingMessageListPanel);
					
					listWaitingMessages = new JList<Thread>();
					listWaitingMessages.setEnabled(false);
					listWaitingMessages.setCellRenderer(new MessageView());
					GridBagConstraints gbc_listWaitingMessages = new GridBagConstraints();
					gbc_listWaitingMessages.fill = GridBagConstraints.BOTH;
					gbc_listWaitingMessages.gridx = 0;
					gbc_listWaitingMessages.gridy = 0;
					waitingMessageListPanel.add(listWaitingMessages, gbc_listWaitingMessages);
				
					scrollPaneWaitingMessages.setViewportView(waitingMessageListPanel);
				}
				JLabel waitingLabel = new JLabel("Read Messages Waiting For Response");
				waitingLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
				GridBagConstraints gbc_waitingLabel = new GridBagConstraints();
				gbc_waitingLabel.gridx = 0;
				gbc_waitingLabel.gridy = 0;
				waitingMessagePanel.add(waitingLabel, gbc_waitingLabel);
				
				GridBagConstraints gbc_scrollPaneWaitingMessages = new GridBagConstraints();
				gbc_scrollPaneWaitingMessages.fill = GridBagConstraints.BOTH;
				gbc_scrollPaneWaitingMessages.gridx = 0;
				gbc_scrollPaneWaitingMessages.gridy = 1;
				waitingMessagePanel.add(scrollPaneWaitingMessages, gbc_scrollPaneWaitingMessages);
			}
			serviceQueueView.setRightComponent(waitingMessagePanel);
		}
	}
	
	public void setView(ServiceQueue serviceQueue){
		Collections.sort(serviceQueue.getUnread(), new Messages.ThreadComparator());
		Collections.sort(serviceQueue.getWaiting(), new Messages.ThreadComparator());
		listNewMessages.setListData(serviceQueue.getUnread().toArray(new Thread[serviceQueue.getUnread().size()]));
		listWaitingMessages.setListData(serviceQueue.getWaiting().toArray(new Thread[serviceQueue.getWaiting().size()]));
	}
	
	/**
	 * Sets the threads of a message type that are overdue as selected
	 *
	 * @param thread the thread
	 * @param type the type
	 */
	public void markOverdue(Thread thread, Messages.Type type){
		JList<Thread> list;
		switch(type){
			case UNREAD:
				list = listNewMessages;
				break;
			case WAITING:
				list = listWaitingMessages;
				break;
			default:
				return;
		}
		ListModel<Thread> model = list.getModel();
		List<Integer> indexList = new ArrayList<>();
		for(int i = 0; i < model.getSize(); i++){
			if(thread.getId().equals(model.getElementAt(i).getId())){
				indexList.add(i);
			}
		}
		int[] overdueIndices = new int[indexList.size()];
		for(int i = 0; i < overdueIndices.length; i++){
			overdueIndices[i] = indexList.get(i);
		}
		list.setSelectedIndices(overdueIndices);
	}
	
	public void setRenderer(ListCellRenderer<Thread> renderer){
		listNewMessages.setCellRenderer(renderer);
		listWaitingMessages.setCellRenderer(renderer);
	}
}
