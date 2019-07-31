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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

import com.bigdata.dis.data.iface.request.GetPartitionCursorRequest;
import com.bigdata.dis.data.iface.request.GetRecordsRequest;
import com.bigdata.dis.data.iface.response.GetPartitionCursorResult;
import com.bigdata.dis.data.iface.response.GetRecordsResult;
import com.bigdata.dis.data.iface.response.Record;
import com.bigdata.dis.sdk.DIS;
import com.bigdata.dis.sdk.DISClientBuilder;
import com.bigdata.dis.sdk.exception.DISClientException;
import com.bigdata.dis.sdk.util.PartitionCursorTypeEnum;
import java.util.Properties;
import java.io.IOException;



public class ConsumerDemo
{
    private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class);
    
    public static void main(String args[])  throws IOException
    {
        runConsumerDemo();
    }
    
    private static void runConsumerDemo()  throws IOException
    {
        Properties consumerConfig = Config.getDisConfig();
        
        String streamName = consumerConfig.getProperty("stream");
        String endpoint = consumerConfig.getProperty("endpoint");
        String ak = consumerConfig.getProperty("ak");
        String sk = consumerConfig.getProperty("sk");
        String projectId = consumerConfig.getProperty("projectId");                                


        // init DIS
        DIS dic = DISClientBuilder.standard()
            .withEndpoint(endpoint)
            .withAk(ak)
            .withSk(sk)
            .withProjectId(projectId)
            .withRegion("eu-de")
            .build();
        
        // Partition key
        String partitionId = "0";
        
        // start Seq num (0 was NOT OK)
        String startingSequenceNumber = "15";
        
        // cursor type
        String cursorType = PartitionCursorTypeEnum.AT_SEQUENCE_NUMBER.name();
        
        try
        {
            // Partition Cursor
            GetPartitionCursorRequest request = new GetPartitionCursorRequest();
            request.setStreamName(streamName);
            request.setPartitionId(partitionId);
            request.setStartingSequenceNumber(startingSequenceNumber);
            request.setCursorType(cursorType);
            GetPartitionCursorResult response = dic.getPartitionCursor(request);
            String cursor = response.getPartitionCursor();
            
            log.info("Get stream {}[partitionId={}] cursor success : {}", streamName, partitionId, cursor);
            
            GetRecordsRequest recordsRequest = new GetRecordsRequest();
            GetRecordsResult recordResponse = null;
            while (true)
            {
                recordsRequest.setPartitionCursor(cursor);
                recordsRequest.setLimit(2);
                recordResponse = dic.getRecords(recordsRequest);
                // get next record
                cursor = recordResponse.getNextPartitionCursor();
                
                for (Record record : recordResponse.getRecords())
                {
                    System.out.println(new String(record.getData().array()));
                    log.info("Get Record [{}], partitionKey [{}], sequenceNumber [{}].",
                        new String(record.getData().array()),
                        record.getPartitionKey(),
                        record.getSequenceNumber());
                }
                Thread.sleep(1000);
            }
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
            System.out.println(e.getMessage());
            System.out.println(e);
            log.error("Failed to access endpoint. Error message [{}]", e.getMessage(), e);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println(e);
            log.error(e.getMessage(), e);
        }
    }
}
