package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.lang.ProcessHandle.Info;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.crypto.Data;

import com.google.protobuf.ByteString;

import org.apache.log4j.Logger;

public class Worker {
  private int id;
  private final Logger LOG = Logger.getLogger(Worker.class);
  private final CoordinatorGrpc.CoordinatorBlockingStub blockingStub;
  private final CoordinatorGrpc.CoordinatorStub asyncStub;

  public Worker(Channel channel) {
    blockingStub = CoordinatorGrpc.newBlockingStub(channel);
    asyncStub = CoordinatorGrpc.newStub(channel);
  }

  public void start() throws ClassNotFoundException, IOException {
    
    DiscoverRequest messageDiscover = DiscoverRequest.newBuilder().setIsAvailible(true).build();
    DiscoverResult response;
    try {
      response = blockingStub.discoverWorker(messageDiscover);
      LOG.info("Sent DiscoverRequest to Coordinator");
    } catch (StatusRuntimeException e) {
      LOG.error("RPC failed " + e);
      return;
    }
    
    LOG.info("Recieved DiscoverResult from Coordinator");
    id = response.getWorkerId();
    FlowMessage flowMessageRequest = FlowMessage.newBuilder().setIsWorkAvailble(true).build();
    FlowMessage flowMessageResponse;
    try {
      flowMessageResponse =  blockingStub.workAvailble(flowMessageRequest);
      LOG.info("Sent flowMessagerRequest to Coordinator");
    } catch (StatusRuntimeException e) {
      LOG.error("RPC failed " + e);
      return;
    }

    while(flowMessageResponse.getIsWorkAvailble()) {
      ControlMessage matrixRequest = ControlMessage.newBuilder().setType(ControlMessageType.AVAILABLE).build();
      DataMessage matrixResponse;
      try {
        matrixResponse = blockingStub.requestCompute(matrixRequest);
        LOG.info("Sent matrix resource request to Coordinator");
      } catch (StatusRuntimeException e) {
      LOG.error("RPC failed " + e);
      return;
      }
      LOG.info("Recieved matrix resource from coordinator");
      Matrix A = Matrix.fromByteString(matrixResponse.getA());
      Matrix B = Matrix.fromByteString(matrixResponse.getB());
      Matrix C = Matrix.like(A);

      new ThreadPooledNaiveParallelMultiplication()
      .multiply(A, B, C);

      ResultMessage resultMessage =
      ResultMessage.newBuilder()
      .setIndex(matrixResponse.getIndex())
      .setC(C.toByteString())
      .build();
      ControlMessage computeResponse;
      LOG.info("Sending compute result back to Coordinator");
      try {
        computeResponse = blockingStub.sendResult(resultMessage);
        if(!computeResponse.getSucceed()) {
          LOG.info("Sending computation failed");
        }
      } catch (StatusRuntimeException e) {
        LOG.error("RPC failed " + e);
      }
      try {
        flowMessageResponse =  blockingStub.workAvailble(flowMessageRequest);
        LOG.info("Sent flowMessagerRequest to Coordinator");
      } catch (StatusRuntimeException e) {
        LOG.error("RPC failed " + e);
        return;
      }
    }
    return;
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
