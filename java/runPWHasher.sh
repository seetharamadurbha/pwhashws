#java -classpath ./target/classes com.durbha.jc.pwhasher.Main -port 1234 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6006
java -Djava.util.logging.config.file=logging.properties -Xdebug -Xrunjdwp:transport=dt_socket,address=6006,server=y,suspend=n -classpath ./target/classes com.durbha.jc.pwhasher.Main -port 1234 -ipAddress localhost
