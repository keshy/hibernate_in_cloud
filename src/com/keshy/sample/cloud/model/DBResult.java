package com.keshy.sample.cloud.model;

/**
 * Database response object created analogous to the S3 Result object.
 * Useful for reporting the response back to the client in a uniform way
 */
public class DBResult {
   
   private String _errorMessage;
   
   private Exception _exception;
   
   private ResultStatus _stat;
   
   public static enum ResultStatus {
      ok, fail;
      
      @Override
      public String toString() {
         return super.toString().toLowerCase();
      }
   }
   
   public void setErrorMessage(String errorMsg) {
      _errorMessage = errorMsg;
   }
   
   public String getErrorMessage() {
      return _errorMessage;
   }
   
   public void setException(Exception exp) {
      _exception = exp;
   }
   
   public Exception getException() {
      return _exception;
   }
   
   public void setStat(ResultStatus status) {
      _stat = status;
   }
   
   public ResultStatus getStat() {
      return _stat;
   }
}
