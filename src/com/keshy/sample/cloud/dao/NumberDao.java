package com.keshy.sample.cloud.dao;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.keshy.sample.cloud.model.NumberVO;
import com.keshy.sample.hibernate.HibernateUtil;


/**
 * DAO to handle all interaction with DB using HQL
 */
public final class NumberDao {
   
   private static final Logger LOG = Logger.getLogger(NumberDao.class.getName());
   
   private NumberDao() {
      
   }
   
   public static boolean addNumber(NumberVO vo) {
      if (vo == null) {
         return false;
      }
      Session session = HibernateUtil.getSessionFactory().openSession();
      Transaction tx = null;
      try {
         tx = session.beginTransaction();
         session.saveOrUpdate(vo);
         tx.commit();
         return true;
      } catch (Exception e) {
         if (tx != null)
            tx.rollback();
         LOG.log(Level.INFO, "exception - " + e.getMessage());
         return false;
      } finally {
         session.close();
      }
   }
}
