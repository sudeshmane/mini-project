package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class SNSEventHandler implements RequestHandler<SNSEvent, Void> {
    @Override
    public Void handleRequest(SNSEvent event, Context context) {
        event.getRecords().forEach(record -> {
//            String messageBody = record.getBody();
        	String messageBody = record.getEventSource();
            context.getLogger().log("Received message: " + messageBody);
            // Process the message
        });
        return null;
    }
}
