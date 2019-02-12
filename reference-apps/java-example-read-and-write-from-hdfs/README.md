Example for read & write into HDFS
==================

Package for the example : mvn package and get the package in target.

Usage in local MRS servers:

 - $ mvn package
 - login to one of the MRS node 
 - $ source /opt/client/bigdata_env
 - $ yarn jar target/example-java-read-and-write-from-hdfs-1.0-SNAPSHOT-jar-with-dependencies.jar

Usage via MRS Gui:

 - mvn package (in local, to generate jar file)
 - upload the jar (target/example-java-read-and-write-from-hdfs-1.0-SNAPSHOT-jar-with-dependencies.jar) to OBS bucket 
 - login to OTC console / Service List / Mapreduce Service / Clusters / Select Your Cluster
 - Job Managment / Jobs / Create / create new Mapreduce
 - select URL from OBS 
 - Select "OK"
 - Job List view / View Log / See the output of the JOB 

Usage Kerberized  MRS Cluster:

 - On the MRS management console, click Cluster.
 - In the Active Cluster list, click the specified cluster name.Record AZ, VPC, and Floating IP Address of the cluster, and Default Security Group of the Master node.
 - On the ECS management console, create a new ECS.Ensure that AZ, VPC, and Security Group of the ECS are the same as those of the cluster to be accessed.Select a Windows public image. For example, select the Enterprise_Windows_STD_2012R2_* enterprise image.For details about other parameter configurations, see Elastic Cloud Server User Guide > Getting Started > Creating an ECS.
 - On the VPC management console, apply for an EIP and bind it to the ECS. the Virtual Private Cloud User Guide > Network Components > EIP > Assigning an EIP and Binding It to an ECS).
 - Log in to the ECS. The account, password, EIP, and security group configuration rules of the Windows system are required for logging in to the ECS. For details about how to log in to the ECS, see Elastic Cloud Server User Guide > Getting Started > Logging In to an ECS > Logging In to a Windows ECS Using a Password (MSTSC).
  - On the Windows remote desktop, use your browser to access MRS Manager. In the browser address bar, enter https://Floating IP Address:28443/web. Enter the name and password of the MRS cluster user, for example, user admin.
  - Login to MRS Manager and Accounts 
  - Go to user list view and select "admin" / More / "Download authentication credential"
  - Untar the credential file and upload this file and load to /tmp folder (tar -xvf admin_*_keytab.tar )
  - init kerberos: kinit admin - type your admin Kerberos password
  - Additional points equals than using NON Kerberized Cluster

