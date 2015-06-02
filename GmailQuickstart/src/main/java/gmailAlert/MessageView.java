/*
 * Swing component that displays a gmail.message as a JPanel
 * view contains:
 * 		1) Email of who the message was from
 * 		2) Time in hh:mm:ss of when the message was received
 * 		3) Snippit of the email
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
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Thread;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * The Class MessageView.
 * 
 * @implements ListCellRenderer<Thread> that displays the most current message in each thread as an instance of this
 */
public class MessageView extends JPanel implements ListCellRenderer<Thread>{
	private static final long serialVersionUID = 1L;
	
	private JLabel lblFrom;
	private JLabel lblDateReceived;
	private JLabel lblSnippit;
	
	private DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
	
	/**
	 * Instantiates a new message view.
	 */
	public MessageView() {
		initiliaze();
	}
	
	/**
	 * Instantiates a new message view.
	 *
	 * @param message the message
	 */
	public MessageView(Message message) {
		this();
		setMessage(message);
	}
	
	private void initiliaze(){
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		setPreferredSize(new Dimension(250, 65));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lblFrom = new JLabel("From:");
		GridBagConstraints gbc_lblFrom = new GridBagConstraints();
		gbc_lblFrom.anchor = GridBagConstraints.WEST;
		gbc_lblFrom.insets = new Insets(0, 0, 5, 5);
		gbc_lblFrom.gridx = 0;
		gbc_lblFrom.gridy = 0;
		add(lblFrom, gbc_lblFrom);
		
		lblDateReceived = new JLabel("Received:");
		GridBagConstraints gbc_lblDateReceived = new GridBagConstraints();
		gbc_lblDateReceived.anchor = GridBagConstraints.EAST;
		gbc_lblDateReceived.insets = new Insets(0, 0, 5, 0);
		gbc_lblDateReceived.gridx = 1;
		gbc_lblDateReceived.gridy = 0;
		add(lblDateReceived, gbc_lblDateReceived);
		
		lblSnippit = new JLabel("Text:");
		GridBagConstraints gbc_lblSnippit = new GridBagConstraints();
		gbc_lblSnippit.anchor = GridBagConstraints.WEST;
		gbc_lblSnippit.gridheight = 2;
		gbc_lblSnippit.insets = new Insets(0, 0, 5, 0);
		gbc_lblSnippit.gridwidth = 2;
		gbc_lblSnippit.gridx = 0;
		gbc_lblSnippit.gridy = 1;
		add(lblSnippit, gbc_lblSnippit);
	}
	
	public void setMessage(Message message) {
		String snippit = "<html>";
		for(MessagePartHeader header : message.getPayload().getHeaders()){
			switch(header.getName()){
				case Messages.FROM_HEADER:
					lblFrom.setText(header.getValue());
					break;
				case Messages.DATE_HEADER:
					try {
						lblDateReceived.setText(formatter.format(Messages.DATE_FORMATTER.parse(header.getValue())));
					} catch (ParseException e) {
						// Format has changed
						e.printStackTrace();
					}
					break;
				case Messages.SUBJECT_HEADER:
					snippit += header.getValue();
					break;
			}
		}
		snippit += "<br/>" + message.getSnippet() + "</html>";
		lblSnippit.setText(snippit);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<? extends Thread> list, Thread value, int index, boolean isSelected, boolean cellHasFocus) {
		setMessage(Messages.getLatestMessage(value));
		
		// Highlights selected element with a red border
		if(isSelected){
			setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		}
		
		return this;
	}
}
