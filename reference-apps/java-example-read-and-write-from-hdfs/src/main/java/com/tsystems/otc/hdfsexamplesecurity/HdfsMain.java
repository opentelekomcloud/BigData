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
package com.tsystems.otc.hdfsexamplesecurity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//import com.mrs.hadoop.security.LoginUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockStoragePolicySpi;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;


public class HdfsMain {

        private static final String STORAGE_POLICY_HOT = "HOT";
        private FileSystem fSystem; /* HDFS file system */
        private static Configuration conf;
        private String DEST_PATH = "/user/hdfs-examples";
        private String FILE_NAME = "test.txt";

        private static String PRNCIPAL_NAME = "admin";

        // PLEASE LOAD YOUR CREDENTIAL 
        private static String PATH_TO_KEYTAB = "/tmp/user.keytab";
        private static String PATH_TO_KRB5_CONF = "/tmp/krb5.conf";

        private static String PATH_TO_HDFS_SITE_XML = "/opt/client/HDFS/hadoop/etc/hadoop/hdfs-site.xml";
        private static String PATH_TO_CORE_SITE_XML = "/opt/client/HDFS/hadoop/etc/hadoop/core-site.xml";

        // private static String PATH_TO_SMALL_SITE_XML =
        // HdfsMain.class.getClassLoader().getResource("smallfs-site.xml").getPath();

        /**
         * HDFS operator instance
         *
         * @throws Exception
         *
         */
        public void examples() throws Exception {
                // init HDFS FileSystem instance
                init(); // login from here

                // create directory
                mkdir();

                // write file
                write();

                // append file
                append();

                // read file
                read();

                // delete file
                delete();

                // delete directory
                rmdir();
        }

        /**
         * init get a FileSystem instance
         *
         * @throws java.io.IOException
         */
        private void init() throws IOException {
                confLoad();
                authentication();
                instanceBuild();
        }

        /**
         *
         * Add configuration file if the application run on the linux ,then need
         * make the path of the core-site.xml and hdfs-site.xml to in the linux
         * client file
         *
         */
        private void confLoad() throws IOException {
                conf = new Configuration();
                // conf file
                conf.addResource(new Path(PATH_TO_HDFS_SITE_XML));
                conf.addResource(new Path(PATH_TO_CORE_SITE_XML));
                // conf.addResource(new Path(PATH_TO_SMALL_SITE_XML));
        }

        /**
         * kerberos security authentication if the application running on Linux,need
         * the path of the krb5.conf and keytab to edit to absolute path in Linux.
         * make the keytab and principal in example to current user's keytab and
         * username
         *
         */
        private void authentication() throws IOException {
                // security mode
                System.out.println("security mode: " + conf.get("hadoop.security.authentication") );
                
                if ("kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication"))) {
                        System.setProperty("java.security.krb5.conf", PATH_TO_KRB5_CONF);
                        LoginUtil.login(PRNCIPAL_NAME, PATH_TO_KEYTAB, PATH_TO_KRB5_CONF, conf);
                }
        }

        /**
         * build HDFS instance
         */
        private void instanceBuild() throws IOException {
                // get filesystem
                fSystem = FileSystem.get(conf);
        }

        /**
         * delete directory
         *
         * @throws java.io.IOException
         */
        private void rmdir() throws IOException {
                Path destPath = new Path(DEST_PATH);
                if (!deletePath(destPath)) {
                        System.err.println("failed to delete destPath " + DEST_PATH);
                        return;
                }

                System.out.println("success to delete path " + DEST_PATH);

        }

        /**
         * create directory
         *
         * @throws java.io.IOException
         */
        private void mkdir() throws IOException {
                Path destPath = new Path(DEST_PATH);
                if (!createPath(destPath)) {
                        System.err.println("failed to create destPath " + DEST_PATH);
                        return;
                }

                System.out.println("success to create path " + DEST_PATH);
        }

        /**
         * set storage policy to path
         *
         * @param policyName
         *            Policy Name can be accepted:
         *            <li>HOT
         *            <li>WARN
         *            <li>COLD
         *            <li>LAZY_PERSIST
         *            <li>ALL_SSD
         *            <li>ONE_SSD
         * @throws java.io.IOException
         */
        private void setStoragePolicy(String policyName) throws IOException {
                if (fSystem instanceof DistributedFileSystem) {
                        DistributedFileSystem dfs = (DistributedFileSystem) fSystem;
                        Path destPath = new Path(DEST_PATH);
                        Boolean flag = false;

                        mkdir();
                        BlockStoragePolicySpi[] storage = dfs.getStoragePolicies();
                        for (BlockStoragePolicySpi bs : storage) {
                                if (bs.getName().equals(policyName)) {
                                        flag = true;
                                }
                                System.out.println("StoragePolicy:" + bs.getName());
                        }
                        if (!flag) {
                                policyName = storage[0].getName();
                        }
                        dfs.setStoragePolicy(destPath, policyName);
                        System.out.println("succee to set Storage Policy path " + DEST_PATH);
                        rmdir();
                } else {
                        System.out.println("SmallFile not support to set Storage Policy !!!");
                }
        }

        /**
         * create file,write file
         *
         * @throws java.io.IOException
         * @throws com.huawei.bigdata.hdfs.examples.ParameterException
         */
        private void write() throws IOException, ParameterException {
                final String content = "hi, I am bigdata. It is successful if you can see me.";
                InputStream in = (InputStream) new ByteArrayInputStream(content.getBytes());
                try {
                        HdfsWriter writer = new HdfsWriter(fSystem, DEST_PATH + File.separator + FILE_NAME);
                        writer.doWrite(in);
                        System.out.println("success to write.");
                } finally {
                        // make sure the stream is closed finally.
                        close(in);
                }
        }

        /**
         * append file content
         *
         * @throws java.io.IOException
         */
        private void append() throws Exception {
                final String content = "I append this content.";
                InputStream in = (InputStream) new ByteArrayInputStream(content.getBytes());
                try {
                        HdfsWriter writer = new HdfsWriter(fSystem, DEST_PATH + File.separator + FILE_NAME);
                        writer.doAppend(in);
                        System.out.println("success to append.");
                } finally {
                        // make sure the stream is closed finally.
                        close(in);
                }
        }

        /**
         * read file
         *
         * @throws java.io.IOException
         */
        private void read() throws IOException {
                String strPath = DEST_PATH + File.separator + FILE_NAME;
                Path path = new Path(strPath);
                FSDataInputStream in = null;
                BufferedReader reader = null;
                StringBuffer strBuffer = new StringBuffer();

                try {
                        in = fSystem.open(path);
                        reader = new BufferedReader(new InputStreamReader(in));
                        String sTempOneLine;

                        // write file
                        while ((sTempOneLine = reader.readLine()) != null) {
                                strBuffer.append(sTempOneLine);
                        }

                        System.out.println("result is : " + strBuffer.toString());
                        System.out.println("success to read.");

                } finally {
                        // make sure the streams are closed finally.
                        close(reader);
                        close(in);
                }
        }

        /**
         * delete file
         *
         * @throws java.io.IOException
         */
        private void delete() throws IOException {
                Path beDeletedPath = new Path(DEST_PATH + File.separator + FILE_NAME);
                if (fSystem.delete(beDeletedPath, true)) {
                        System.out.println("success to delete the file " + DEST_PATH + File.separator + FILE_NAME);
                } else {
                        System.out.println("failed to delete the file " + DEST_PATH + File.separator + FILE_NAME);
                }
        }

        /**
         * close stream
         *
         * @param stream
         * @throws java.io.IOException
         */
        private void close(Closeable stream) throws IOException {
                stream.close();
        }

        /**
         * create file path
         *
         * @param filePath
         * @return
         * @throws java.io.IOException
         */
        private boolean createPath(final Path filePath) throws IOException {
                if (!fSystem.exists(filePath)) {
                        fSystem.mkdirs(filePath);
                }
                return true;
        }

        /**
         * delete file path
         *
         * @param filePath
         * @return
         * @throws java.io.IOException
         */
        private boolean deletePath(final Path filePath) throws IOException {
                if (!fSystem.exists(filePath)) {
                        return false;
                }
                // fSystem.delete(filePath, true);
                return fSystem.delete(filePath, true);
        }

        public static void main(String[] args) throws Exception {
                HdfsMain hdfs_examples = new HdfsMain();
                hdfs_examples.examples();
                // set policy
                System.out.println("begin to set Storage Policy");
                hdfs_examples.setStoragePolicy(STORAGE_POLICY_HOT);
                System.out.println("set Storage Policy end");
        }

}
