# GMonitor

######Developed by: Brian Martyn

Java swing application that monitors a G-mail account for unread messages and messages that are read but un-replied to.  Application sends an alert for all messages in each category that stay in that state for a given threshold

#####Main: [**_ServiceQueueController_**](https://github.com/b-martyn/GMonitor/blob/master/GmailQuickstart/src/main/java/gmailAlert/ServiceQueueController.java)

#####Design Patterns

1. *Factory*

  **_ServiceQueueFactory_**
  
    1. Contains a singleton instance of a Gmail service
    2. Used to obtain an instance of a ServiceQueue model
    3. Used to obtain a full formatted Gmail.Thread and Gmail.Message

2. *MVC*

  1. Model: [**_ServiceQueue_**](https://github.com/b-martyn/GMonitor/blob/master/GmailQuickstart/src/main/java/gmailAlert/ServiceQueue.java)
    * List of Gmail.Threads that are unread
    * List of Gmail.Threads that are read but not replied to
  2. View: [**_ServiceQueueView_**](https://github.com/b-martyn/GMonitor/blob/master/GmailQuickstart/src/main/java/gmailAlert/ServiceQueueView.java)
    1. Main View: [**_MessageView_**](https://github.com/b-martyn/GMonitor/blob/master/GmailQuickstart/src/main/java/gmailAlert/MessageView.java) ListCellRenderer
      * who the message was from
      * what time it was received
      * subject of message
      * snippit of email
    2. Alert View: [**_OverdueMessageView_**](https://github.com/b-martyn/GMonitor/blob/master/GmailQuickstart/src/main/java/gmailAlert/OverdueMessageView.java) ListCellRenderer
      * who the message was from
      * how many days, hours, minutes, seconds message is overdue
  3. Controller: [**_ServiceQueueController_**](https://github.com/b-martyn/GMonitor/blob/master/GmailQuickstart/src/main/java/gmailAlert/ServiceQueueController.java)
      * threshold for unread messages and un-serviced messages
      * alert creation
