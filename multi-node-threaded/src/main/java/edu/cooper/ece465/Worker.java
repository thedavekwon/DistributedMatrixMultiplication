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
        return;
      }
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
    // private final Logger LOG = Logger.getLogger(WorkerImpl.class);

    // @Override
    // public StreamObserver<DataMessage> requestCompute(StreamObserver<DataMessage> rObserver) {
    //   return new StreamObserver<DataMessage>() {
    //     @Override
    //     public void onNext(DataMessage dataMessage) {
    //       try {
    //         Matrix tempA = Matrix.fromByteString(dataMessage.getA());
    //         Matrix tempB = Matrix.fromByteString(dataMessage.getB());
    //       } catch (ClassNotFoundException e) {
    //         LOG.error("Class not Found");
    //         e.printStackTrace();
    //       } catch (IOException e) {
    //         LOG.error("DataMessage could not be converted");
    //         e.printStackTrace();
    //       }
          
    //     }

    //     @Override
    //     public void onError(Throwable t) {
    //       LOG.error("Error while returning computation");
    //     }

    //     @Override
    //     public void onCompleted() {
    //       LOG.info("Sent computation back");
    //       rObserver.onCompleted();
    //     }
    //   };
    // }

    @Override
    public void control(ControlMessage controlMessage, StreamObserver<ControlMessage> rObserver) {
      if (controlMessage.getType() == ControlMessageType.AVAILABLE) {

      } else if (controlMessage.getType() == ControlMessageType.KILL) {
        
      }
    }
  }
}
