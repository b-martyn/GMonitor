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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import com.google.api.services.gmail.model.Thread;

import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

/**
 * The Class ServiceQueueView.
 */
public class ServiceQueueView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JList<Thread> listNewMessages;
	private JList<Thread> listWaitingMessages;
	private JScrollPane scrollPaneNewMessages;
	private JScrollPane scrollPaneWaitingMessages;
	
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
			scrollPaneNewMessages = new JScrollPane();
			scrollPaneNewMessages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			{
				JLabel unreadLabel = new JLabel("Pending Unread Messages");
				unreadLabel.setHorizontalAlignment(SwingConstants.CENTER);
				scrollPaneNewMessages.setColumnHeaderView(unreadLabel);
				
				listNewMessages = new JList<Thread>();
				listNewMessages.setEnabled(false);
				
				scrollPaneNewMessages.setViewportView(listNewMessages);
			}
			serviceQueueView.setLeftComponent(scrollPaneNewMessages);
		}
		{
				
			scrollPaneWaitingMessages = new JScrollPane();
			scrollPaneWaitingMessages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			{
				JLabel waitingLabel = new JLabel("Read Messages Waiting For Response");
				waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);
				scrollPaneWaitingMessages.setColumnHeaderView(waitingLabel);
				
				listWaitingMessages = new JList<Thread>();
				listWaitingMessages.setEnabled(false);
				
				scrollPaneWaitingMessages.setViewportView(listWaitingMessages);
			}
			serviceQueueView.setRightComponent(scrollPaneWaitingMessages);
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
		if(renderer instanceof JComponent){
			JComponent component = (JComponent)renderer;
			if(component.getPreferredSize() != null){
				scrollPaneNewMessages.setPreferredSize(component.getPreferredSize());
				scrollPaneWaitingMessages.setPreferredSize(component.getPreferredSize());
			}
		}
		
	}
}
