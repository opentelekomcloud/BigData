Example for using DMS Kafka service native interface 
==================

Package for the example : mvn package and get the package in target.

Usage in OTC server

Compilation of the project:

 - $ mvn package

Using the package: 

 Initialization of the project: 
# fetch project binary to get latest dist packages and configurations
 - wget https://obs.eu-de.otc.t-systems.com/dms-demo/KafkaDemo.zip
 - unzip KafkaDemo.zip  -d /tmp
 - mvn package (in local, to generate jar file)
 - cp -R /tmp/KafkaDemo/dist . 
 - cd dist
 - vim config/consumer.properties 
   - bootstrap.servers=dms-kafka.eu-de.otc.t-systems.com:37000
   - set your kafka topic id 
   - set your kafka group id 
 - vim config/producer.properties 
   - bootstrap.servers=dms-kafka.eu-de.otc.t-systems.com:37000
   - set your kafka topic id 
   - set your kafka group id 
 - vim config/dms_kafka_client_jaas.conf  
   - set your AK key  
   - set your SK key
   - set your PROJECT ID
 
 Usage:
   Consumer:
 - ./consume.sh  
   Producer:
 - ./produce.sh  
