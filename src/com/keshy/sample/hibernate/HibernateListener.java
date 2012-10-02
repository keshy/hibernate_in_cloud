package com.keshy.sample.hibernate;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class HibernateListener implements ServletContextListener {
   
   // On servlet startup create single instance of the session factory
   public void contextInitialized(ServletContextEvent event) {
      HibernateUtil.getSessionFactory();
   }
   
   // destroy the session once the servlet has been taken down
   public void contextDestroyed(ServletContextEvent event) {
      HibernateUtil.getSessionFactory().close();
   }
}
