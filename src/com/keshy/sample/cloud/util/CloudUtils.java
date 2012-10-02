package com.keshy.sample.cloud.util;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.keshy.sample.cloud.model.DBResult;
import com.keshy.sample.cloud.model.DBResult.ResultStatus;


/**
 * Utility class supporting basic string manipulation and object to map
 * conversion
 * 
 */
public final class CloudUtils {
   
   public static enum Method {
      INSERTNUMBER((short)0);
      
      private short _val;
      
      private Method(short ordinal) {
         this._val = ordinal;
      }
      
      public short getValue() {
         return this._val;
      }
      
      @Override
      public String toString() {
         return super.toString().toLowerCase();
      }
      
      public static Method find(String v) {
         if (v == null) {
            return null;
         }
         v = v.toLowerCase();
         for (Method d: values()) {
            if (d.toString().equals(v)) {
               return d;
            }
         }
         return null;
      }
   }
   
   public static Method getMethod(String path) {
      if (path == null) {
         return null;
      }
      // Trim leading and trailing slashes
      String pathInfo = trim(path, '/');
      
      int n = pathInfo.indexOf('/');
      return Method.find(n < 0 ? pathInfo : pathInfo.substring(0, n));
   }
   
   public static String trim(String s, char c) {
      if (s == null)
         return s;
      while (!empty(s) && s.charAt(0) == c) {
         s = s.substring(1);
      }
      
      while (!empty(s) && s.charAt(s.length() - 1) == c) {
         s = s.substring(0, s.length() - 1);
      }
      return s;
   }
   
   public static boolean empty(String s) {
      return s == null || s.length() == 0;
   }
   
   public static Map<String, String> getResultMap(Object result) {
      if (result == null) {
         return null;
      }
      Map<String, String> map = new HashMap<String, String>();
      if (result instanceof PutObjectResult) {
         PutObjectResult res = (PutObjectResult)result;
         map.put("eTag", res.getETag() == null ? null : res.getETag());
         map.put("type", "S3Upload");
         map.put("stat", "ok");
      }
      else if (result instanceof AmazonClientException) {
         // error
         AmazonClientException exp = (AmazonClientException)result;
         map.put("errorMessage", exp.getMessage() == null ? null : exp.getMessage());
         map.put("type", "amazonClientException");
         map.put("stat", "fail");
      }
      else if (result instanceof AmazonServiceException) {
         AmazonServiceException exp = (AmazonServiceException)result;
         map.put("errorMessage", exp.getMessage() == null ? null : exp.getMessage());
         map.put("httpStatusCode", "" + exp.getStatusCode() == null ? null : "" + exp.getStatusCode());
         map.put("errorCode", exp.getErrorCode() == null ? null : exp.getErrorCode());
         map.put("type", "amazonServiceException");
         map.put("stat", "fail");
      }
      else if (result instanceof DBResult) {
         DBResult res = (DBResult)result;
         if (res != null && res.getStat() != null) {
            if (res.getStat().equals(ResultStatus.ok)) {
               map.put("type", "DBUpdate");
               map.put("stat", ResultStatus.ok.toString());
            }
            else if (res.getStat().equals(ResultStatus.fail)) {
               map.put("type", "DBUpdate");
               map.put("errorMsg", res.getErrorMessage() == null ? null : res.getErrorMessage());
               map.put("exception", res.getException() == null ? null : res.getException().toString());
               map.put("stat", ResultStatus.fail.toString());
            }
         }
         
      }
      else if (result instanceof Exception) {
         Exception exp = (Exception)result;
         map.put("type", "exception");
         map.put("msg", exp.getMessage() == null ? null : exp.getMessage().toString());
         map.put("stat", "fail");
      }
      return map;
   }
}
