package org.subhash.kinesis.client.service;

//import com.amazonaws.services.kinesis.AmazonKinesis;
//import com.amazonaws.services.kinesis.model.PutRecordsRequest;
//import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
//import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.subhash.kinesis.client.AwsKinesisClient;
import org.subhash.kinesis.client.model.Order;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class KinesisService {

    @Autowired
    KinesisConsumerService kinesisConsumerService;
    @Autowired
    private AwsKinesisClient awsKinesisClient;
    List<String> productList = new ArrayList<>();
    Random random = new Random();


     public KinesisService(AwsKinesisClient awsKinesisClient) {
         this.awsKinesisClient = awsKinesisClient;

    }

    public void  produceData() throws InterruptedException {
        populateProductList();

        int i=0;
        do{
            sendData();
            Thread.sleep(1000);
            i++;
        }while(i <10);

        kinesisConsumerService.getKinesisDataStream();
    }



    public void sendData() throws InterruptedException {

        PutRecordsRequest request = PutRecordsRequest.builder()
                .streamName("OrderStream-1")
                .records(getRecordsRequestList())
                .build();
        PutRecordsResponse response = awsKinesisClient.getKinesisClient().putRecords(request);

        System.out.println("Successfully sent " +
                (getRecordsRequestList().size() - response.failedRecordCount()) + " of " + getRecordsRequestList().size());

        // Print individual responses (optional)
        List<PutRecordsResultEntry> resultEntries = response.records();
        for (int i = 0; i < resultEntries.size(); i++) {
            PutRecordsResultEntry resultEntry = resultEntries.get(i);
            if (resultEntry.errorCode() != null) {
                System.out.println("Record " + i + " failed with error: " + resultEntry.errorMessage());
            } else {
                System.out.println("Record " + i + " succeeded with Seq#: " + resultEntry.sequenceNumber());
            }
        }

    }

    public List<PutRecordsRequestEntry> getRecordsRequestList(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<PutRecordsRequestEntry> recordEntries = new ArrayList<>();

        for (Order order: getOrderList()){
            PutRecordsRequestEntry entry = PutRecordsRequestEntry.builder()
                    .partitionKey(UUID.randomUUID().toString())  // Use a unique or hashed key for shard distribution
                    .data(SdkBytes.fromUtf8String(gson.toJson(order)))
                    .build();
            recordEntries.add(entry);
        }
        return recordEntries;
    }

    public List<Order> getOrderList(){
        List<Order> orders = new ArrayList<>();
        for(int i=1;i<=500;i++){
            Order order = new Order();
            order.setOrderId(Math.abs(random.nextInt()));
            order.setProduct(productList.get(random.nextInt(productList.size())));
            order.setQuantity(random.nextInt(20));
            orders.add(order);
        }
        return orders;
    }

    private void populateProductList(){
        productList.add("shirt");
        productList.add("t-shirt");
        productList.add("shorts");
        productList.add("tie");
        productList.add("shoes");
        productList.add("jeans);");
        productList.add("belt");
    }


}
