/*
 * JDialog containing a ServiceQueueView that displays each Thread as overdue
 * 
 * @author Brian Martyn
 * Copyright 2015
 */
package gmailAlert;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Class ServiceQueueOverdueAlert.
 */
public class ServiceQueueOverdueAlert extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private ServiceQueueView view;
	
	/**
	 * Instantiates a new service queue overdue alert.
	 *
	 * @param owner the controller of this
	 */
	public ServiceQueueOverdueAlert(Frame owner) {
		super(owner);
		initiliaze();
	}
	
	/**
	 * Instantiates a new service queue overdue alert.
	 *
	 * @param owner the controller of this
	 * @param serviceQueue the service queue
	 */
	public ServiceQueueOverdueAlert(Frame owner, ServiceQueue serviceQueue) {
		this(owner);
		setView(serviceQueue);
	}
	
	private void initiliaze(){
		setTitle("Service Queue Overdue Alert");
		setBounds(300, 50, 450, 350);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0};
		gbl_contentPanel.rowHeights = new int[]{0};
		gbl_contentPanel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			view = new ServiceQueueView();
			view.setRenderer(new OverdueMessageView());
			GridBagConstraints gbc_view = new GridBagConstraints();
			gbc_view.fill = GridBagConstraints.BOTH;
			gbc_view.gridx = 0;
			gbc_view.gridy = 0;
			contentPanel.add(view, gbc_view);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnView = new JButton("View Queue");
				btnView.setActionCommand("View Queue");
				btnView.addActionListener(this);
				buttonPane.add(btnView);
				getRootPane().setDefaultButton(btnView);
			}
			{
				JButton btnClose = new JButton("Close");
				btnClose.setActionCommand("Close");
				btnClose.addActionListener(this);
				buttonPane.add(btnClose);
			}
		}
	}
	
	public void setView(ServiceQueue serviceQueue){
		view.setView(serviceQueue);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		switch(actionEvent.getActionCommand()){
			case "View Queue":
				getOwner().setVisible(true);
				dispose();
				break;
			case "Close":
				dispose();
				break;
			default:
				System.out.println("Missed action command: " + actionEvent.getActionCommand());
		}
	}

}
