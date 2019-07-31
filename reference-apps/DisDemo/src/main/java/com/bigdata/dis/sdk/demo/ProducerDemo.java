/*
 * Copyright 2018 T-systems
 * @version 1.0
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.bigdata.dis.sdk.demo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

import com.bigdata.dis.data.iface.request.PutRecordsRequest;
import com.bigdata.dis.data.iface.request.PutRecordsRequestEntry;
import com.bigdata.dis.data.iface.response.PutRecordsResult;
import com.bigdata.dis.data.iface.response.PutRecordsResultEntry;
import com.bigdata.dis.sdk.DIS;
import com.bigdata.dis.sdk.DISClientBuilder;
import com.bigdata.dis.sdk.exception.DISClientException;
import java.util.Properties;
import java.io.IOException;


public class ProducerDemo
{
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);
    
    public static void main(String args[])  throws IOException
    {
        runProduceDemo();
    }
    
    private static void runProduceDemo()  throws IOException
    {
        Properties consumerConfig = Config.getDisConfig();
        
        String streamName = consumerConfig.getProperty("stream");
        String endpoint = consumerConfig.getProperty("endpoint");
        String ak = consumerConfig.getProperty("ak");
        String sk = consumerConfig.getProperty("sk");
        String projectId = consumerConfig.getProperty("projectId");


        // Init DIS builder
        DIS dic = DISClientBuilder.standard()
            .withEndpoint(endpoint)
            .withAk(ak)
            .withSk(sk)
            .withProjectId(projectId)
            .withRegion("eu-de")
            .build();

        // DIS message
        String message = "hello world.";
        
        PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
        putRecordsRequest.setStreamName(streamName);
        List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<PutRecordsRequestEntry>();
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        for (int i = 0; i < 3; i++)
        {
            PutRecordsRequestEntry putRecordsRequestEntry = new PutRecordsRequestEntry();
            putRecordsRequestEntry.setData(buffer);
            putRecordsRequestEntry.setPartitionKey(String.valueOf(ThreadLocalRandom.current().nextInt(1000000)));
            putRecordsRequestEntryList.add(putRecordsRequestEntry);
        }
        putRecordsRequest.setRecords(putRecordsRequestEntryList);
        
        log.info("========== BEGIN PUT ============");
        System.out.println("begin put");
        PutRecordsResult putRecordsResult = null;
        try
        {
            putRecordsResult = dic.putRecords(putRecordsRequest);
        }
        catch (DISClientException e)
        {
            System.out.println(e.getMessage());
            System.out.println(e);
            log.error("Failed to get a normal response, please check params and retry. Error message [{}]",
                e.getMessage(),
                e);
        }
        catch (ResourceAccessException e)
        {
            System.out.println("Failed to access endpoint. Error message [{}]"+ e.getMessage());
            log.error("Failed to access endpoint. Error message [{}]", e.getMessage(), e);
        }
        catch (Exception e)
        {
            System.out.println(" Error message "+ e);
            log.error("Failed to access endpoint. Error message [{}]",e.getMessage(),e);
        }
        
        if (putRecordsResult != null)
        {
            log.info("Put {} records[{} successful / {} failed].",
                putRecordsResult.getRecords().size(),
                putRecordsResult.getRecords().size() - putRecordsResult.getFailedRecordCount().get(),
                putRecordsResult.getFailedRecordCount());
            
            for (int j = 0; j < putRecordsResult.getRecords().size(); j++)
            {
                PutRecordsResultEntry putRecordsRequestEntry = putRecordsResult.getRecords().get(j);
                if (putRecordsRequestEntry.getErrorCode() != null)
                {
                    //  PUT ERROR
                    System.out.println(" put failed "+ putRecordsRequestEntry.getErrorMessage() );
                    log.error("[{}] put failed, errorCode [{}], errorMessage [{}]",
                        new String(putRecordsRequestEntryList.get(j).getData().array()),
                        putRecordsRequestEntry.getErrorCode(),
                        putRecordsRequestEntry.getErrorMessage());
                }
                else
                {
                    System.out.println(" put ok "+ putRecordsRequestEntry.getSequenceNumber() );
                    // put ok partition, key, seq
                    log.info("[{}] put success, partitionId [{}], partitionKey [{}], sequenceNumber [{}]",
                        new String(putRecordsRequestEntryList.get(j).getData().array()),
                        putRecordsRequestEntry.getPartitionId(),
                        putRecordsRequestEntryList.get(j).getPartitionKey(),
                        putRecordsRequestEntry.getSequenceNumber());
                }
            }
        }
        System.out.println(" end put");
        log.info("========== END PUT ============");
    }
    
}
