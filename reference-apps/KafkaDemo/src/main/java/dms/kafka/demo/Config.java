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

public class Config
{
    private static final String PRODUCE_CONFIG = "producer.properties";

    private static final String CONSUME_CONFIG = "consumer.properties";

    private static final String SASL_CONFIG = "dms_kafka_client_jaas.conf";

    private static final String TRUSTSTORE_PATH = "client.truststore.jks";

    public static Properties getProducerConfig() throws IOException
    {
        return getPropertyFromClassPath(PRODUCE_CONFIG);
    }

    public static Properties getConsumerConfig() throws IOException
    {
        return getPropertyFromClassPath(CONSUME_CONFIG);
    }

    public static String getSaslConfig() throws IOException
    {
        return getClassLoader().getResource(SASL_CONFIG).getPath();
    }

    public static String getTrustStorePath() throws IOException
    {
        return getClassLoader().getResource(TRUSTSTORE_PATH).getPath();
    }

    private static Properties getPropertyFromClassPath(String resourceName) throws IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
        {
            classLoader = Config.class.getClassLoader();
        }

        Properties properties = new Properties();
        properties.load(classLoader.getResourceAsStream(resourceName));
        return properties;
    }

    private static ClassLoader getClassLoader()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
        {
            classLoader = Config.class.getClassLoader();
        }
        return classLoader;
    }

}
