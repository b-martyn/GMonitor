/*
 * Swing component that displays a gmail.message as a JPanel
 * view contains:
 * 		1) Email of who the message was from
 * 		2) Time in dd:hh:mm:ss of how overdue the message is
 * 
 * @author Brian Martyn
 * Copyright 2015
 */
package gmailAlert;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Date;

/**
 * The Class OverdueMessageView.
 * 
 * @implements ListCellRenderer<Thread> that displays the most current message in each thread as an instance of this
 */
public class OverdueMessageView extends JPanel implements ListCellRenderer<Thread>{
	
	private static final long serialVersionUID = 1L;
	
	private JLabel lblFrom;
	private JLabel lblOverdueAmount;
	
	/**
	 * Instantiates a new overdue message view.
	 */
	public OverdueMessageView() {
		initiliaze();
	}
	
	/**
	 * Instantiates a new overdue message view.
	 *
	 * @param message the message
	 */
	public OverdueMessageView(Message message) {
		this();
		setMessage(message);
	}
	
	private void initiliaze(){
		setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		setPreferredSize(new Dimension(250, 30));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lblFrom = new JLabel("");
		GridBagConstraints gbc_lblFrom = new GridBagConstraints();
		gbc_lblFrom.anchor = GridBagConstraints.WEST;
		gbc_lblFrom.insets = new Insets(0, 0, 0, 5);
		gbc_lblFrom.gridx = 0;
		gbc_lblFrom.gridy = 0;
		add(lblFrom, gbc_lblFrom);
		
		lblOverdueAmount = new JLabel("");
		GridBagConstraints gbc_lblOverdueAmount = new GridBagConstraints();
		gbc_lblOverdueAmount.anchor = GridBagConstraints.EAST;
		gbc_lblOverdueAmount.gridx = 1;
		gbc_lblOverdueAmount.gridy = 0;
		add(lblOverdueAmount, gbc_lblOverdueAmount);
	}
	
	public void setMessage(Message message) {
		lblFrom.setText(Messages.getMessageSender(message));
		
		Date receivedDate = Messages.getReceivedDate(message);
		Date current = new Date();
		long overdueAmount = current.getTime() - receivedDate.getTime();
		overdueAmount /= 1000;
		int seconds = (int)overdueAmount % 60;
		overdueAmount /= 60;
		int minutes = (int)overdueAmount % 60;
		overdueAmount /= 60;
		int hours = (int)overdueAmount % 24;
		int days = (int)overdueAmount / 24;
		if(hours == 0){
			lblOverdueAmount.setText(String.format("%d:%02d min", minutes, seconds));
		}else if(days == 0){
			lblOverdueAmount.setText(String.format("%d:%02d:%02d hours", hours, minutes, seconds));
		}else{
			lblOverdueAmount.setText(String.format("%d:%02d:%02d:%02d days", days, hours, minutes, seconds));
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<? extends Thread> list, Thread value, int index, boolean isSelected, boolean cellHasFocus) {
		setMessage(Messages.getLatestMessage(value));
		return this;
	}
}
