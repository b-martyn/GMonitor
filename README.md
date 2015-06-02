# GMonitor

######Developed by: Brian Martyn

Java swing application that monitors a G-mail account for unread messages and messages that are read but un-replied to.  Application sends an alert for all messages in each category that stay in that state for a given threshold

#####Main: **_ServiceQueueController_**

#####Design Patterns

1. *Factory*

  **_ServiceQueueFactory_**
  
    1. Contains a singleton instance of a Gmail service
    2. Used to obtain an instance of a ServiceQueue model
    3. Used to obtain a full formatted Gmail.Thread and Gmail.Message

2. *MVC*

  1. Model: **_ServiceQueue_**
  2. View: **_ServiceQueueView_**
  3. Controller: **_ServiceQueueController_**
