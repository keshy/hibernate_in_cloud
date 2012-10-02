package com.keshy.sample.cloud;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.keshy.sample.cloud.dao.NumberDao;
import com.keshy.sample.cloud.model.CloudConstants;
import com.keshy.sample.cloud.model.DBResult;
import com.keshy.sample.cloud.model.NumberVO;
import com.keshy.sample.cloud.model.StorageFile;
import com.keshy.sample.cloud.model.DBResult.ResultStatus;
import com.keshy.sample.cloud.util.CloudUtils;
import com.keshy.sample.cloud.util.CloudUtils.Method;


public class CloudServlet extends HttpServlet {
   
   /** serialVersionUID */
   private static final long serialVersionUID = -6588479723620983650L;
   
   private static final Logger LOG = Logger.getLogger(CloudServlet.class.getName());
   
   private static final AWSCredentials MY_CRED = new BasicAWSCredentials(CloudConstants.AWS_PUBLIC_KEY,
            CloudConstants.AWS_PRIVATE_KEY);
   
   private AmazonS3Client _s3Client;
   
   @Override
   public void init(ServletConfig config) {
      LOG.log(Level.INFO, "Server started...");
   }
   
   @Override
   public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
      LOG.log(Level.INFO, "CloudServlet: doGet()");
      try {
         Method method = CloudUtils.getMethod(req.getPathInfo());
         if (method == null) {
            LOG.log(Level.SEVERE, "no method specified");
            throw new NoSuchMethodException("no method specified");
         }
         
         switch (method) {
            case INSERTNUMBER: {
               processInsertOperation(req, res);
               return;
            }
               // other methods can go here..
            default:
               LOG.log(Level.WARNING, "Unsupported method received in request : " + method);
               throw new NoSuchMethodException("Unsupported method received in request : " + method);
         }
      } catch (Exception exp) {
         // format response to JSON and send it back to client
         res.getOutputStream().write(CloudUtils.getResultMap(exp).toString().getBytes());
      } finally {
         res.flushBuffer();
      }
      
   }
   
   private void processInsertOperation(HttpServletRequest req, HttpServletResponse res) throws IOException {
      LOG.log(Level.INFO, "method insert()");
      try {
         if (req.getParameter(CloudConstants.NUMBER) == null) {
            throw new InvalidParameterException("must provide parameter 'number' to insert");
            
         }
         
         String numberInString = req.getParameter(CloudConstants.NUMBER);
         Long number = null;
         number = Long.parseLong(numberInString);
         if (number != null) {
            // update local mysql on EC2
            DBResult dbResult = updateDB(number);
            // update S3 account associated with this webapp
            Object S3Result = uploadToS3(number);
            // Collect results to prepare JSON response
            List<Map<String, String>> results = new ArrayList<Map<String, String>>();
            results.add(CloudUtils.getResultMap(dbResult));
            results.add(CloudUtils.getResultMap(S3Result));
            
            try {
               res.getOutputStream().write(JSONArray.fromArray(results.toArray()).toString().getBytes());
            } catch (IOException exp) {
               LOG.log(Level.WARNING, "IOException encountered while writing response");
               throw new IOException("error encountered while writing response.");
            } finally {
               // close outputStream
               res.flushBuffer();
            }
         }
      } catch (NumberFormatException exp) {
         throw new InvalidParameterException("The value provided for number is not a valid number");
      } 
   }
   
   private Object uploadToS3(Long number) {
      /*
       * data on file in S3 would look like: Number: -33
       * TimeStamp:1345072297151
       */

      String data = "Number: " + number + "\nTimeStamp:" + System.currentTimeMillis();
      Long length = (long)data.getBytes().length;
      ByteArrayInputStream stream = new ByteArrayInputStream(data.getBytes());
      if (getS3Client() == null) {
         _s3Client = new AmazonS3Client(MY_CRED);
         setS3Client(_s3Client);
      }
      StorageFile file = new StorageFile();
      file.setFileName("Number-" + number.toString());
      file.setFilePath(file.getFileName());
      file.setLength(length.longValue());
      
      PutObjectRequest request = new PutObjectRequest(CloudConstants.S3_BUCKET, file.getFilePath(), stream,
               file.getObjectMetadata());
      Object result = null;
      try {
         result = getS3Client().putObject(request);
      } catch (AmazonServiceException ase) {
         LOG.log(Level.SEVERE, "Error Message:    " + ase.getMessage());
         LOG.log(Level.SEVERE, "HTTP Status Code: " + ase.getStatusCode());
         LOG.log(Level.SEVERE, "AWS Error Code:   " + ase.getErrorCode());
         LOG.log(Level.SEVERE, "Error Type:       " + ase.getErrorType());
         LOG.log(Level.SEVERE, "Request ID:       " + ase.getRequestId());
         result = ase;
      } catch (AmazonClientException ace) {
         LOG.log(Level.SEVERE, "Error Message: " + ace.getMessage());
         result = ace;
      }
      return result;
   }
   
   private DBResult updateDB(Long number) {
      DBResult result = new DBResult();
      if (number == null) {
         String error = "No number available to update the DB.";
         result.setErrorMessage(error);
         result.setException(new InvalidParameterException());
         result.setStat(ResultStatus.fail);
         LOG.log(Level.INFO, error);
         return result;
      }
      NumberVO vo = new NumberVO();
      vo.setNumber(number);
      vo.setTimestamp(new Timestamp(System.currentTimeMillis()));
      if (NumberDao.addNumber(vo)) {
         LOG.log(Level.INFO, "DB update for number : " + number + " is compelte.");
         result.setStat(ResultStatus.ok);
         return result;
      }
      else {
         LOG.log(Level.INFO, "No number available to update in DB.");
         String error = "DB update error";
         result.setErrorMessage(error);
         result.setException(new InvalidParameterException());
         result.setStat(ResultStatus.fail);
         LOG.log(Level.INFO, error);
         return result;
      }
   }
   
   private void setS3Client(AmazonS3Client s3Client) {
      _s3Client = s3Client;
   }
   
   private AmazonS3Client getS3Client() {
      return _s3Client;
   }
   
}
