package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.commons.Matrix.MatrixIndexes;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class Worker {
  private final Logger LOG = Logger.getLogger(Worker.class);
  private final CoordinatorGrpc.CoordinatorBlockingStub blockingStub;

  public Worker(Channel channel) {
    blockingStub = CoordinatorGrpc.newBlockingStub(channel);
  }

  public void start() throws ClassNotFoundException, IOException {
    DataMessage message = DataMessage.newBuilder().setType(DataMessageType.SEND_REQUEST).build();
    DataMessage response;
    try {
      response = blockingStub.requestResource(message);
    } catch (StatusRuntimeException e) {
      LOG.error("RPC failed " + e);
      return;
    }
    Matrix A = Matrix.fromByteString(response.getA());
    Matrix B = Matrix.fromByteString(response.getB());
    
    LOG.info(response);
    int index = response.getIndex();
    Indexes indexes = response.getIndexes();
    Matrix C = Matrix.like(A);
    LOG.info(MatrixIndexes.fromIndexes(indexes).toString());
    new SerialMatrixMultiplication()
        .multiplyWithMatrixIndexes(A, B, C, MatrixIndexes.fromIndexes(indexes));
    LOG.info(C.toString());
    DataMessage resultMessage =
        DataMessage.newBuilder()
            .setType(DataMessageType.SEND_RESULT)
            .setIndex(index)
            .setIndexes(indexes)
            .setA(C.toByteString())
            .build();
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
}
