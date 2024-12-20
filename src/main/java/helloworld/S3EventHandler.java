package helloworld;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import helloworld.S3EventHandler.CustomEvent;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

public class S3EventHandler implements RequestHandler<CustomEvent, Void> {
	
	public static class CustomEvent {
		public CustomEvent(){
			
		}
	    private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	@Override
	public Void handleRequest(CustomEvent input, Context context) {
		try {
			String url = "http://34.229.238.184:8080/db/verify";
	        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
	
	        // Optional default is GET
	        httpClient.setRequestMethod("GET");
	
	        // Add request header
	        httpClient.setRequestProperty("User-Agent", "MyApp/1.0");
	
	        int responseCode = httpClient.getResponseCode();
	        System.out.println("GET Response Code :: " + responseCode);
	
	        if (responseCode == HttpURLConnection.HTTP_OK) { // success
	            BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();
	
	            // Print result
	            System.out.println(response.toString());
	        } else {
	            System.out.println("GET request not worked");
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	 public class CustomEventHandler implements RequestHandler<CustomEvent, String> {
//		    @Override
//		    public String handleRequest(CustomEvent event, Context context) {
//		        // Handle custom event
//		        return "Handled custom event: " + event.getMessage();
//		    }
//		}
	
//    @Override
//    public Void handleRequest(CustomEvent event, Context context) {
//        event.getRecords().forEach(record -> {
//            String messageBody = record.getBody();
//            
//            context.getLogger().log("Received message: " + messageBody);
//        	
//            sendtoTopic(context, topicArn, " sending as SQS-LAMBDA-SNS "+messageBody);
//            
//        });
//        return null;
//    }

	 
    public void sendtoTopic(Context context, String topicArn, String message) {
    	context.getLogger().log("snsclient preparation to topic: " + message);
    	
    	context.getLogger().log("preparation request to topic: " + message);
	    PublishRequest request = PublishRequest.builder().topicArn(topicArn).message(message).build();
	    
	    context.getLogger().log("Sending to topic: " + message);
		PublishResponse result = snsClient.publish(request);
		
		context.getLogger().log("sent to topic: " + result.messageId());
    }
   
    
    
    
    
	SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1)
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("AKIAYHJANCWHTWVOYSJ7","Uq7Ew7+oqX8h2UBJ3NFGiu1019a22rkn9pJC46CN")))
			.build();
	final String topicArn = "arn:aws:sns:us-east-1:565393036687:ProjectName-UploadsNotificationTopic";
	



	 

}

