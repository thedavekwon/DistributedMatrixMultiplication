package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class Worker {
  private int id;
  private static final Logger LOG = Logger.getLogger(Worker.class);
  private final CoordinatorGrpc.CoordinatorBlockingStub blockingStub;
  private static Properties p = new Properties();

  static {
    try {
      p.load(Worker.class.getClassLoader().getResourceAsStream("grpc.properties"));
    } catch (IOException e) {
      LOG.error(e);
    }
  }

  public Worker(Channel channel) {
    blockingStub = CoordinatorGrpc.newBlockingStub(channel);
  }

  public void start() throws ClassNotFoundException, IOException {
    DiscoverRequest discoverRequest = DiscoverRequest.newBuilder().build();
    DiscoverResult response;
    try {
      response = blockingStub.discoverWorker(discoverRequest);
      LOG.info("Sent DiscoverRequest to Coordinator");
    } catch (StatusRuntimeException e) {
      LOG.error("RPC failed " + e);
      return;
    }
    LOG.info("Recieved DiscoverResult from Coordinator");
    id = response.getWorkerId();

    ControlMessage matrixRequest =
        ControlMessage.newBuilder().setType(ControlMessageType.AVAILABLE).setWorkerId(id).build();
    DataMessage matrixResponse;
    try {
      matrixResponse = blockingStub.requestCompute(matrixRequest);
      LOG.info("Sent matrix resource request to Coordinator");
    } catch (StatusRuntimeException e) {
      LOG.error("RPC failed " + e);
      return;
    }

    while (matrixResponse.getIsWorkAvailable()) {
      LOG.info("Recieved matrix resource from coordinator");
      Matrix A = Matrix.fromByteString(matrixResponse.getA());
      Matrix B = Matrix.fromByteString(matrixResponse.getB());
      Matrix C = Matrix.like(A);

      new ThreadPooledNaiveParallelMultiplication(8).multiply(A, B, C);

      ResultMessage resultMessage =
          ResultMessage.newBuilder()
              .setIndex(matrixResponse.getIndex())
              .setType(ResultMessageType.SUCCEED)
              .setWorkerId(id)
              .setC(C.toByteString())
              .build();
      ControlMessage computeResponse;
      LOG.info("Sending compute result back to Coordinator");
      try {
        computeResponse = blockingStub.sendResult(resultMessage);
      } catch (StatusRuntimeException e) {
        LOG.error("RPC failed " + e);
        try {
          computeResponse =
              blockingStub.sendResult(
                  ResultMessage.newBuilder()
                      .setIndex(matrixResponse.getIndex())
                      .setType(ResultMessageType.FAILED)
                      .setWorkerId(id)
                      .build());
        } catch (Exception ex) {
          LOG.error("RPC failed " + ex);
        }
      }
      // To demonstrate unstable network
      try {
        Thread.sleep(2000);
      } catch (Exception e) {
        LOG.error(e);
      }
      try {
        matrixResponse = blockingStub.requestCompute(matrixRequest);
        LOG.info("Sent matrix resource request to Coordinator");
      } catch (StatusRuntimeException e) {
        LOG.error("RPC failed " + e);
        return;
      }
    }
    return;
  }

  public static void main(String[] args) throws Exception {
    String target = p.getProperty("host") + ":" + p.getProperty("port");

    ManagedChannel channel =
        ManagedChannelBuilder.forTarget(target)
            .maxInboundMessageSize(128 * 1024 * 1024)
            .usePlaintext()
            .build();
    try {
      Worker worker = new Worker(channel);
      worker.start();
    } finally {
      channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }
}
