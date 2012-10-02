package com.keshy.sample.cloud.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.keshy.sample.cloud.model.CloudConstants;
import com.keshy.sample.cloud.model.DBResult;
import com.keshy.sample.cloud.util.CloudUtils;


public class CloudUtilsTest {
   
   @Test
   public void testMethodEnumFind() {
      assertEquals(null, CloudUtils.Method.find("LISTFOLDER"));
   }
   
   @Test
   public void testMethodEnumFindNull() {
      assertEquals(null, CloudUtils.Method.find(null));
   }
   
   @Test
   public void testGetMethod() {
      assertEquals(null, CloudUtils.getMethod("/user/system"));
      assertNotSame(null, CloudUtils.getMethod("/insertNumber"));
      assertNotSame(CloudUtils.Method.INSERTNUMBER, CloudUtils.getMethod("///"));
      assertEquals(null, CloudUtils.getMethod(null));
   }
   
   @Test
   public void testTrimMethod() {
      assertEquals("insert", CloudUtils.trim("/insert/", '/'));
      assertEquals("test", CloudUtils.trim("/test///", '/'));
      assertNotSame("test", CloudUtils.trim("/te*st///", '*'));
      assertEquals(null, CloudUtils.trim(null, '*'));
   }
   
   @Test
   public void testEmptyMethod() {
      assertEquals(true, CloudUtils.empty(null));
      assertEquals(true, CloudUtils.empty(""));
      assertNotSame(true, CloudUtils.empty("a"));
   }
   
   @Test
   public void testGetResultMapMethod() {
      assertEquals(null, CloudUtils.getResultMap(null));
      assertEquals("amazonClientException", CloudUtils.getResultMap(new AmazonClientException(null)).get("type"));
      // special test case as AmazonServiceException extends
      // AmazonClientException
      assertNotSame("amazonServiceException", CloudUtils.getResultMap(new AmazonServiceException(null)).get("type"));
      assertNotSame("S3Upload", CloudUtils.getResultMap(new PutObjectRequest(CloudConstants.S3_BUCKET, null, null))
               .get("type"));
      // no DB related information passed resulting in an empty map
      assertEquals(0, CloudUtils.getResultMap(new DBResult()).size());
      assertEquals("exception", CloudUtils.getResultMap(new Exception()).get("type"));
   }
}
