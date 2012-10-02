package com.keshy.sample.cloud.model;

import com.amazonaws.services.s3.model.ObjectMetadata;


/**
 * 
 * Class representing an S3 object with the necessary information used by
 * the SDK to do the upload
 */
public class StorageFile {
   
   private String _fileName;
   
   private String _filePath;
   
   private Long _fileSize;
   
   public void setFileName(String fileName) {
      _fileName = fileName;
   }
   
   public String getFileName() {
      return _fileName;
   }
   
   public void setFilePath(String path) {
      _filePath = path;
   }
   
   public String getFilePath() {
      return _filePath;
   }
   
   public void setLength(Long size) {
      _fileSize = size;
   }
   
   public Long getLength() {
      return _fileSize;
   }
   
   // currently only supporting Content-Length and COntent-Type.
   public ObjectMetadata getObjectMetadata() {
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(_fileSize);
      metadata.setContentType("text/plain");
      return metadata;
   }
}
