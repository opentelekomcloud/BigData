#!/bin/bash
# Which java to use
if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

base_dir=$(cd "$(dirname "$0")"; pwd)

for file in $base_dir/libs/*.jar;
do
  basefilename=`basename "$file"`
  CLASSPATH="$CLASSPATH\:libs/$basefilename"
done

CLASSPATH=";$CLASSPATH:target/com.bigdata.dis.sdk.demo-0.0.1-SNAPSHOT.jar"
#CMD="java -cp \"$CLASSPATH\" com.bigdata.dis.sdk.demo.ProducerDemo"
CMD="\"$JAVA\" -cp \"$CLASSPATH\" com.bigdata.dis.sdk.demo.ConsumerDemo"
echo $CMD
$CMD