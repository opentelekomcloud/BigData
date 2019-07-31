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

import java.io.IOException;
import java.util.Properties;

public class Config
{
    private static final String DIS_CONFIG = "dis.properties";

    public static Properties getDisConfig() throws IOException
    {
        return getPropertyFromClassPath(DIS_CONFIG);
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
