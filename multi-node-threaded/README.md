# Multi Node Threaded Matrix Multiplication
This implementation includes distributing the matrix multiplication algorithm to multiple nodes. The structure invloves two types of nodes: Coordinators and Workers. Coordinators are responsible for pooling the availible Workers that announce themseleves to the Coordinator. The Coordinator then splits the matrix up and sends the partial matricies that each Worker needs to perform its individual computation. Each split is recorded as a task in a task queue to keep track of each computation that is needed. The Worker requests the resources and then computes the resultant sub matrix. The Worker then sends the result back to the Coordinator.

Grpc operates through protocol buffers, servers, and stubs. Stubs are initialized on the client application and can send messages to the server application. Servers can accept messages and have services implemented to handle client calls and to return specific messages back. For our implementation, the Coordinater has three rpc methods that the Worker can call though an interface. Each call made to the Coordinater by a Worker sends a message that can be translated on both sides. Once a call is made, the Coordinator will send back the approprate response. An example would be the discoverWorker function that accepts a DiscoverRequest message from the client and returns a DiscoverResult message from the server. Each message can be populated by various fields that are defined in the proto file. A detailed explaination is included in the message.proto in the commons folder. For fault tolerance, workers send whether computations are stuck or failed to the coordinator using type field in ResultMessage. If the coordinator received FAILED type, it adds the following task back to the queue and reassigns the task to other workers. 

To simulate the network latency, we made the worker to wait 2 seconds before requesting new task. We have not compared the comptutation speed, since we are running from a single machine using multiple processes. For the experimentation (run.sh), we have spawned three workers and one coordinator. 

We have added a compression to the byte array to reduce the size of the message transmitted through grpc, and we increased the maximum message size of the grpc. However, even with compression, very large matricies can not be sent because grpc is not designed for sending large messages. To improve this, we plan to chunk matrices with the stream message and assemble them. 

## Usage
```
# Multi Node Threaded
# Coordinator
java -cp multi-node-threaded/target/multi-node-threaded-0.0.1-jar-with-dependencies.jar edu.cooper.ece465.Coordinator

# Worker
java -cp multi-node-threaded/target/multi-node-threaded-0.0.1-jar-with-dependencies.jar edu.cooper.ece465.Worker

# Coordinator and 3 Workers
```