package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

public class SQSEventHandler implements RequestHandler<SQSEvent, Void> {
	
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        event.getRecords().forEach(record -> {
            String messageBody = record.getBody();
            
            context.getLogger().log("Received message: " + messageBody);
        	
            sendtoTopic(context, topicArn, " sending as SQS-LAMBDA-SNS "+messageBody);
            
        });
        return null;
    }
    
    
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
