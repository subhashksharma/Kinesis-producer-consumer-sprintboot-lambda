package org.subhash.kinesis.client;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;

@Component
public class AwsKinesisClient {

    public static final String AWS_ACCESS_KEY = "aws.accessKeyId";
    public static final String AWS_SECRET_KEY = "aws.secretKey";

    static {
        //add your AWS account access key and secret key
        System.setProperty(AWS_ACCESS_KEY, System.getenv("AWS_ACCESS_KEY_ID"));
        System.setProperty(AWS_SECRET_KEY, System.getenv("AWS_SECRET_ACCESS_KEY"));
    }

    public KinesisClient getKinesisClient() {
        return KinesisClient.builder()
                .region(Region.AP_SOUTH_1) // Change this to your region
                .build();
    }
}