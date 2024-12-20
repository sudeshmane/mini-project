package helloworld;

import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3Entity;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationResponse;
import software.amazon.awssdk.services.cloudfront.model.InvalidationBatch;
import software.amazon.awssdk.services.cloudfront.model.Paths;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

public class S3BucketEventHandler implements RequestHandler<S3Event, String> {
	
	@Override
	public String handleRequest(S3Event event, Context context) {
		try {
			 String callerReference = UUID.randomUUID().toString();
			 String distributionId = "E2PS4TYJ981E1L";
			 
			event.getRecords().forEach(record -> {
				S3Entity s3Obj = record.getS3();
				String bucketName = s3Obj.getBucket().getName();
				String objectKey = s3Obj.getObject().getKey();
				long objectSize = s3Obj.getObject().getSize();
				context.getLogger().log("Bucket: " + bucketName + ", KeyName: " + objectKey + ", size: " + objectSize);
				
				AwsBasicCredentials credentials = AwsBasicCredentials.create("AKIAYHJANCWHTWVOYSJ7","Uq7Ew7+oqX8h2UBJ3NFGiu1019a22rkn9pJC46CN");	
				
				CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
		                .region(Region.US_EAST_1) // Change to your region
		                .credentialsProvider(StaticCredentialsProvider.create(credentials))
		                .build();
		                
				
				objectKey = objectKey.replace("+", "%20");
				Paths paths = Paths.builder()
		                .items("/"+objectKey)
		                .quantity(1)
		                .build();
				
				context.getLogger().log("CloudFront Invalidation Path "+paths.items());
						
		        InvalidationBatch invalidationBatch = InvalidationBatch.builder()
		                .paths(paths)
		                .callerReference(callerReference)
		                .build();

		        CreateInvalidationRequest request = CreateInvalidationRequest.builder()
		                .distributionId(distributionId)
		                .invalidationBatch(invalidationBatch)
		                .build();

		        CreateInvalidationResponse response = cloudFrontClient.createInvalidation(request);
		        context.getLogger().log("Cleared files from cloudfront");

			});
			return "Processed " + event.getRecords().size() + " records.";
		} catch (Exception e) {
			e.printStackTrace();
		}
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

