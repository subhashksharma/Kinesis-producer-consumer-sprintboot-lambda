package org.subhash.kinesis.client.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.subhash.kinesis.client.AwsKinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class KinesisConsumerService {

    @Autowired
    private AwsKinesisClient awsKinesisClient;
    List<String> productList = new ArrayList<>();
    String streamName = "OrderStream-1";

    public void getKinesisDataStream() throws InterruptedException {
        // 1. Describe stream to get the shardId
        DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder()
                .streamName(streamName)
                .build();

        DescribeStreamResponse describeStreamResponse = awsKinesisClient.getKinesisClient().describeStream(describeStreamRequest);
        String shardId = describeStreamResponse.streamDescription().shards().get(0).shardId();

        // 2. Get a shard iterator (you can choose TRIM_HORIZON or LATEST)
        GetShardIteratorRequest iteratorRequest = GetShardIteratorRequest.builder()
                .streamName(streamName)
                .shardId(shardId)
                .shardIteratorType(ShardIteratorType.TRIM_HORIZON) // or LATEST
                .build();

        GetShardIteratorResponse iteratorResponse = awsKinesisClient.getKinesisClient().getShardIterator(iteratorRequest);
        String shardIterator = iteratorResponse.shardIterator();

        System.out.println("Starting to consume from shard: " + shardId);

        // 3. Loop to poll for records
        while (true) {
            GetRecordsRequest recordsRequest = GetRecordsRequest.builder()
                    .shardIterator(shardIterator)
                    .limit(500)
                    .build();

            GetRecordsResponse recordsResponse = awsKinesisClient.getKinesisClient().getRecords(recordsRequest);
            List<software.amazon.awssdk.services.kinesis.model.Record> records = recordsResponse.records();

            for (software.amazon.awssdk.services.kinesis.model.Record record : records) {
               // String data = SdkBytes.fromByteBuffer(record.data());
                System.out.println("Received record: " + record.data().asUtf8String());
            }
            // Set next shard iterator for next loop
            shardIterator = recordsResponse.nextShardIterator();
            // Sleep to avoid hitting limits
            Thread.sleep(1000);
        }
    }
}

