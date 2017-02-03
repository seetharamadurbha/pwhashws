# Password Hasher Web Service
This is a simple web service that let's you hash passwords using SHA-512. You do this using two HTTP calls

1. POST to /hash
   Provide the password to hash in the parameters (sent in HTTP POST body using FORM URL Encoding scheme). 
   The specific parameter name is 'password' (without quotes).
   * Content-Type MUST be application/x-www-form-urlencoded
   * Content-Length MUST be present and equal to the POST Body
   * POST Body MUST be encoded as specified in [https://www.w3.org/TR/html401/interact/forms.html#h-17.13.3]

   This call returns a 'unique' number, which is then used to query the hash in a subsequent call. See below.

2. GET to /hash/{sequenceNumber}
   The sequence number is the one gotten in the previous call POST to /hash.
   Some things to note
   * There is a 5 second delay in computing the hash, thus if this call (GET) is made before the hash is computed, a 404 (Not Found) is returned. You may retry if that happens.
   

## Implementation Languages
   This web service is implemented in two languages - Java and Go. You will find the source in the corresponding folders of this project.
   

## Java notes
   Java sources can be found in the 'java' folder. Following instructions apply to that directory.
   Change to java directory.
   `cd java`

### Compile
   The Java source can be compiled using maven. To compile the sources, run the following command
   `mvn clean compile`

### Test
   This implementation comes with comprehensive JUnit test cases. You can run them by the following command
   `mvn test`

### Running the web service
   You can run the web services using the following command

   `java -Djava.util.logging.config.file=logging.properties -classpath ./target/classes com.durbha.jc.pwhasher.Main -port 1234 -ipAddress localhost'
   
   You should see a successful server startup with the following output on the command line.
   `Listening for transport dt_socket at address: 1234
    Starting with configuration: 
	portNumber: 1234
	ipAddress: localhost/127.0.0.1
	numThreads: 10
	maxRequests: 100

    INFO - Log level is FINE [com.durbha.jc.pwhasher.Main init] Feb 03, 11:32am

    INFO - App ready [com.durbha.jc.pwhasher.Main startWithConfig] Feb 03, 11:32am
    Enter exit to terminate:`
   
   Some notes:
   * The above command points to a logging.properties file. You can change the logging leve there.
   * This application uses a single logger 'pwHasher'.
   * Application supports following parameters on the command line
     * -numThreads: Number of threads to use (determines how many requests can be simultaneously processed) [Default 10]
     * -maxRequests: This is the number of requests that can be queued for processing before clients get a 'Service Not Available' error. [Default 100]
     * -port: The port number to listen to [Default 80]
     * -ipAddress: The IP address to bind to [Default localhost]

### Stopping the web service
   Type 'exit' (without the quotes) to stop the web service.
   
   
### Testing
   The service can be tested with any HTTP client (using a browser, for example). But `curl` is probably the easiest from a command line.
   1. Start the server. Please see 'Running the web service' above.
   2. POSTing a request. Run the following command
   `curl -v -d 'password=AngryMonkey' http://localhost:1234/hash`
   Note: The verbose option (-v) is included above, so you can see the details.
   You should see the following output.
   `* Connected to localhost (127.0.0.1) port 1234 (#0)
    > POST /hash HTTP/1.1
    > Host: localhost:1234
    > User-Agent: curl/7.43.0
    > Accept: */*
    > Content-Length: 20
    > Content-Type: application/x-www-form-urlencoded
    > 
    * upload completely sent off: 20 out of 20 bytes
    < HTTP/1.1 200 OK
    < Connection: close
    < Content-Length: 1
    < Date: Fri, 03 Feb 2017 11:33:06 MST
    < Content-Type: text/plain; charset=UTF-8
    < 
    * Closing connection 0
    1`
    
    The '1' (wihout quotes) you see on the last line is the sequence number you use in the next command.
    
    Note: If you do not include the -v option, then curl shows only the sequence number in the output.
    Note: There will not be a new line after the sequence number shown by curl, so your command prompt will come right after the sequence number.
    
    
   3. GETting the hash. Run the following command
   `curl http://localhost:1234/hash/1`
   
   It should come back with the password hash, that looks like
   `a6306201dc431886db117dab3f14f78d234555b6e95c404ebc018d8915bd777d067519cb318460e0e94260c335b2988fd18a41ec1bb362444c9a48d0af74edac`
   
   Note: Again, notice that your command prompt will be printed right after the hash.
      

## Getting statistics
   This implementation keeps track of the number of requests and the average time it took to compute the hash. You can query this information by
   `curl http://localhost:1234/stats`
   
   This GET returns a JSON, that looks like
   `{"total":3, "average":1}`
   
## Implementation notes
1. The 'unique' number returned is actually a running sequence number.
2. No 'authorization' checks performed on GET /hash call. So, anyone can query the hashed password for a given sequence number.
3. A 5 second delay is used in computing the hashes, so if the GET on /hash/{{sequenceNumber}} is issued before that time, service will return a HTTP 404 (Not Found) error.
3. Sequence numbers (and thus their computed hashes) will not persist across server restarts.
4. Sequence numbers will NOT be unique behind a load-balancer, thus this implementation is NOT to be used behind a load-balancer.
