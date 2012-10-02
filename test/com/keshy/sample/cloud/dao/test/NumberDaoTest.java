package com.keshy.sample.cloud.dao.test;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;

import com.keshy.sample.cloud.dao.NumberDao;
import com.keshy.sample.cloud.model.NumberVO;


public class NumberDaoTest {
   
   @Before
   public void setUp() {
   }
   
   @Test
   public void testEmptyVO() {
      assertEquals(false, NumberDao.addNumber(null));
   }
   
   @Test
   public void testNullPrimaryKey() {
      NumberVO vo = new NumberVO();
      vo.setNumber(null);
      vo.setTimestamp(new Timestamp(System.currentTimeMillis()));
      assertEquals(false, NumberDao.addNumber(vo));
   }
   
   @Test
   public void testNullTimeStamp() {
      NumberVO vo = new NumberVO();
      vo.setNumber(23L);
      vo.setTimestamp(null);
      assertEquals(true, NumberDao.addNumber(vo));
   }
}
