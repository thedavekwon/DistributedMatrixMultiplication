package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.commons.Matrix.MatrixIndexes;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;

import org.apache.log4j.Logger;

public class Worker {
  private int id;
  private final Logger LOG = Logger.getLogger(Worker.class);
  private final CoordinatorGrpc.CoordinatorBlockingStub blockingStub;

  public Worker(Channel channel) {
    blockingStub = CoordinatorGrpc.newBlockingStub(channel);
  }

  public void start() throws ClassNotFoundException, IOException {
    DiscoverRequest message = DiscoverRequest.newBuilder().setIsAvailible(true).build();
    DiscoverResult response;
    try {
      response = blockingStub.discoverWorker(message);
    } catch (StatusRuntimeException e) {
      LOG.error("RPC failed " + e);
      return;
    }
    LOG.info("Recieved DiscoverResult");
    id = response.getWorkerId();


    List<DataMessage> responseList = new ArrayList<DataMessage>();
    while (responses.hasNext()) {
      responseList.add(responses.next());
    }
    LOG.info("Recieved response from coordinator");
    ByteString subA = responseList.get(0).getA().concat(responseList.get(1).getA());
    ByteString subB = responseList.get(0).getB().concat(responseList.get(1).getB());
    Matrix A = Matrix.fromByteString(subA);
    Matrix B = Matrix.fromByteString(subB);
    
    int index = responseList.get(0).getIndex();
    Indexes indexes = responseList.get(0).getIndexes();
    Matrix C = Matrix.like(A);
    new ThreadPooledNaiveParallelMultiplication(8)
        .multiplyWithMatrixIndexes(A, B, C, MatrixIndexes.fromIndexes(indexes));
    DataMessage resultMessage =
        DataMessage.newBuilder()
            .setType(DataMessageType.SEND_RESULT)
            .setIndex(index)
            .setIndexes(indexes)
            .setA(C.toByteString())
            .build();
    LOG.info("Sending response back to coordinator");
    try {
      response = blockingStub.sendResult(resultMessage);
    } catch (StatusRuntimeException e) {
      LOG.error("RPC failed " + e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    String target = "localhost:8080";

    ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
    try {
      Worker worker = new Worker(channel);
      worker.start();
    } finally {
      channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  static class WorkerImpl extends WorkerGrpc.WorkerImplBase {
    
    public void requestCompute(DataMessage msg, StreamObserver<DataMessage> rObserver){

    }
  }
}
