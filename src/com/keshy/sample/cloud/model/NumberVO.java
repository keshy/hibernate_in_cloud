package com.keshy.sample.cloud.model;

import java.sql.Timestamp;


/**
 * Object model for the relational mapping with Hibernate. An instance of
 * this class would be mapped directly to the relational table in MySql
 * 
 */
public class NumberVO {
   
   private Long number;
   
   private Timestamp timestamp;
   
   public void setNumber(Long num) {
      number = num;
   }
   
   public Long getNumber() {
      return number;
   }
   
   public void setTimestamp(Timestamp ts) {
      timestamp = ts;
   }
   
   public Timestamp getTimestamp() {
      return timestamp;
   }
   
}
