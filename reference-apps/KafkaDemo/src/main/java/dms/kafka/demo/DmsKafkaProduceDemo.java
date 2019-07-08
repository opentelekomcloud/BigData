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

package dms.kafka.demo;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class DmsKafkaProduceDemo
{
    public static void main(String[] args) throws IOException
    {
        Properties producerConfig = Config.getProducerConfig();

        producerConfig.put("ssl.truststore.location", Config.getTrustStorePath());
        System.setProperty("java.security.auth.login.config", Config.getSaslConfig());

        Producer<String, String> producer = new KafkaProducer<>(producerConfig);
        for (int i = 0; i < 10; i++)
        {
            Future<RecordMetadata> future =
                producer.send(new ProducerRecord<String, String>(
                        producerConfig.getProperty("topic"),
                        null, "hello, dms kafka."));
            RecordMetadata rm;
            try
            {
                rm = future.get();
                System.out.println("Succeed to send msg: " + rm.offset());
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }
        producer.close();
    }
}
