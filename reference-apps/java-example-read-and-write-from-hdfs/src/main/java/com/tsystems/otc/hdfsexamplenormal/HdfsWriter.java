/*
 * Copyright 2018 T-systems
 * @author  Zsolt Nagy
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
package com.tsystems.otc.hdfsexamplenormal;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsWriter {

        private FSDataOutputStream hdfsOutStream;

        private BufferedOutputStream bufferOutStream;

        private FileSystem fSystem;
        private String fileFullName;

        public HdfsWriter(FileSystem fSystem, String fileFullName) throws ParameterException {
                if ((null == fSystem) || (null == fileFullName)) {
                        throw new ParameterException("some of input parameters are null.");
                }

                this.fSystem = fSystem;
                this.fileFullName = fileFullName;
        }

        /**
         * append the inputStream to a file in HDFS
         *
         * @param inputStream
         * @throws IOException
         * @throws ParameterException
         */
        public void doWrite(InputStream inputStream) throws IOException, ParameterException {
                if (null == inputStream) {
                        throw new ParameterException("some of input parameters are null.");
                }

                setWriteResource();
                try {
                        outputToHDFS(inputStream);
                } finally {
                        closeResource();
                }
        }

        /**
         * append the inputStream to a file in HDFS
         *
         * @param inputStream
         * @throws IOException
         * @throws ParameterException
         */
        public void doAppend(InputStream inputStream) throws IOException, ParameterException {
                if (null == inputStream) {
                        throw new ParameterException("some of input parameters are null.");
                }

                setAppendResource();
                try {
                        outputToHDFS(inputStream);
                } finally {
                        closeResource();
                }
        }

        private void outputToHDFS(InputStream inputStream) throws IOException {
                final int countForOneRead = 10240; // 10240 Bytes each time
                final byte buff[] = new byte[countForOneRead];
                int count;

                while ((count = inputStream.read(buff, 0, countForOneRead)) > 0) {
                        bufferOutStream.write(buff, 0, count);
                }

                bufferOutStream.flush();
                hdfsOutStream.hflush();
        }

        private void setWriteResource() throws IOException {
                Path filepath = new Path(fileFullName);
                hdfsOutStream = fSystem.create(filepath);
                bufferOutStream = new BufferedOutputStream(hdfsOutStream);
        }

        private void setAppendResource() throws IOException {
                Path filepath = new Path(fileFullName);
                hdfsOutStream = fSystem.append(filepath);
                bufferOutStream = new BufferedOutputStream(hdfsOutStream);
        }

        private void closeResource() {
                // close hdfsOutStream
                if (hdfsOutStream != null) {
                        try {
                                hdfsOutStream.close();
                        } catch (IOException e) {
                                System.out.println(e);
                        }
                }

                // close bufferOutStream
                if (bufferOutStream != null) {
                        try {
                                bufferOutStream.close();
                        } catch (IOException e) {
                                System.out.println(e);
                        }
                }
        }
}
