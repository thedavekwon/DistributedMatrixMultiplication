package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.xml.crypto.Data;

import com.google.protobuf.ByteString;

import org.apache.log4j.Logger;
import org.javatuples.Pair;
import org.javatuples.Quartet;

public class Coordinator {
  private static final Logger LOG = Logger.getLogger(Coordinator.class);

  private static int N = 64;
  private static int generateWorkerId = 0;
  private static Matrix A = Matrix.createRandomMatrix(N, N);
  private static Matrix B = Matrix.I(N, N);
  private static Matrix C = new Matrix(N, N);
  private static Matrix C1 = new Matrix(N, N);
  private static Matrix C2 = new Matrix(N, N);
  private static ArrayBlockingQueue<Integer> workerQueue;
  private static CoordinatorQueue queue = new CoordinatorQueue();
  private static Server server;

  private void start() throws IOException {
    String propFileName = "grpc.properties";
    Properties p = new Properties();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
    p.load(inputStream);

    server = ServerBuilder.forPort(Integer.parseInt(p.getProperty("port"))).addService(new CoordinatorImpl()).build().start();

    LOG.info("Coordinator started on " + Integer.parseInt(p.getProperty("port")));
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread() {
              @Override
              public void run() {
                try {
                  Coordinator.this.stop();
                } catch (InterruptedException e) {
                  LOG.error("Thread not sucessfully started" + e);
                  e.printStackTrace();
                }
              }
            });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  

  public static void main(String[] args) throws IOException, InterruptedException {
    final Coordinator server = new Coordinator();

    server.start();
    server.blockUntilShutdown();
    LOG.info("Server shutdown");
    C.incrementFromMatrices(C1, C2);
    LOG.info(A.equals(C));
  }

  static class CoordinatorImpl extends CoordinatorGrpc.CoordinatorImplBase {
    @Override
    public void discoverWorker(DiscoverRequest discoverRequest, StreamObserver<DiscoverResult> rObserver) {
      if(!(discoverRequest.getIsAvailible())) return;
      LOG.info("Recieved DiscoverRequest from Worker");
      DiscoverResult response = DiscoverResult.newBuilder().setWorkerId(generateWorkerId).build();
      // workerQueue.add(generateWorkerId++);
      rObserver.onNext(response);
      LOG.info("Sent DiscoverResult response to Worker");
      rObserver.onCompleted();
    }

    @Override
    public void workAvailble(FlowMessage request, StreamObserver<FlowMessage> rObserver) {
      if(!(request.getIsWorkAvailble())) return;
      LOG.info("Recieved workAvailible from Worker");
      Boolean check = true;
      if (queue.isEmpty()){
        check = false;
      }
      FlowMessage response = FlowMessage.newBuilder().setIsWorkAvailble(check).build();
      rObserver.onNext(response);
      LOG.info("Sent workAvaible response to Worker");
      rObserver.onCompleted();
    }

    @Override
    public void requestCompute(ControlMessage controlMessage, StreamObserver<DataMessage> rObserver) {
      if(queue.isEmpty()) return;
      if(!(controlMessage.getType() == ControlMessageType.AVAILABLE)) return;
      LOG.info("Recieved matrix resource request from Worker");
      Quartet<Matrix, Matrix, Matrix, Matrix> splitMatrixA = A.split4();
      Quartet<Matrix, Matrix, Matrix, Matrix> splitMatrixB = B.split4();
      int taskNum = queue.selectTask();
      switch (queue.selectTask()) {
        case 1:
          try {
            DataMessage response = DataMessage.newBuilder().setA(splitMatrixA.getValue0().toByteString())
                .setB(splitMatrixB.getValue0().toByteString()).setIndex(1).build();
            rObserver.onNext(response);
          } catch (IOException e) {
            LOG.error("requestCompute response could not be sent");
            e.printStackTrace();
          }
          break;
        case 2:
          try {
            DataMessage response = DataMessage.newBuilder().setA(splitMatrixA.getValue0().toByteString())
                .setB(splitMatrixB.getValue1().toByteString()).setIndex(2).build();
            rObserver.onNext(response);
          } catch (IOException e) {
            LOG.error("requestCompute response could not be sent");
            e.printStackTrace();
          }
          break;
        case 3:
          try {
            DataMessage response = DataMessage.newBuilder().setA(splitMatrixA.getValue2().toByteString())
                .setB(splitMatrixB.getValue0().toByteString()).setIndex(3).build();
            rObserver.onNext(response);
          } catch (IOException e) {
            LOG.error("requestCompute response could not be sent");
            e.printStackTrace();
          }
          break;
        case 4:
          try {
            DataMessage response = DataMessage.newBuilder().setA(splitMatrixA.getValue2().toByteString())
                .setB(splitMatrixB.getValue1().toByteString()).setIndex(4).build();
            rObserver.onNext(response);
          } catch (IOException e) {
            LOG.error("requestCompute response could not be sent");
            e.printStackTrace();
          }
          break;
        case 5:
          try {
            DataMessage response = DataMessage.newBuilder().setA(splitMatrixA.getValue1().toByteString())
                .setB(splitMatrixB.getValue2().toByteString()).setIndex(5).build();
            rObserver.onNext(response);
          } catch (IOException e) {
            LOG.error("requestCompute response could not be sent");
            e.printStackTrace();
          }
          break;
        case 6:
          try {
            DataMessage response = DataMessage.newBuilder().setA(splitMatrixA.getValue1().toByteString())
                .setB(splitMatrixB.getValue3().toByteString()).setIndex(6).build();
            rObserver.onNext(response);
          } catch (IOException e) {
            LOG.error("requestCompute response could not be sent");
            e.printStackTrace();
          }
          break;
        case 7:
          try {
            DataMessage response = DataMessage.newBuilder().setA(splitMatrixA.getValue3().toByteString())
                .setB(splitMatrixB.getValue2().toByteString()).setIndex(7).build();
            rObserver.onNext(response);
          } catch (IOException e) {
            LOG.error("requestCompute response could not be sent");
            e.printStackTrace();
          }
          break;
        case 8:
          try {
            DataMessage response = DataMessage.newBuilder().setA(splitMatrixA.getValue3().toByteString())
                .setB(splitMatrixB.getValue3().toByteString()).setIndex(8).build();
            rObserver.onNext(response);
          } catch (IOException e) {
            LOG.error("requestCompute response could not be sent");
            e.printStackTrace();
          }
          break;
      }
      LOG.info("Sent matrix resource to Worker");
      queue.removeTask(taskNum);
      rObserver.onCompleted();

    }
    
    @Override
    public void sendResult(ResultMessage msg, StreamObserver<ControlMessage> rObserver) {
      LOG.info("Recieved compute result from Worker");
      Matrix tempC;
      try {
        tempC = Matrix.fromByteString(msg.getC());
      } catch (ClassNotFoundException | IOException e) {
        // TODO Auto-generated catch block
        LOG.error("Unknown class type" + e);
        e.printStackTrace();
        return;
      }
      
      if (msg.getIndex() < 5) {
        C1.incrementFromSubmatrix(tempC, msg.getIndex());
      } else if (msg.getIndex() < 9) {
        C2.incrementFromSubmatrix(tempC, msg.getIndex());
      }
      ControlMessage response = ControlMessage.newBuilder().setType(ControlMessageType.KILL).setSucceed(true).build();
      rObserver.onNext(response);
      LOG.info("Sent success response back to Worker");
      rObserver.onCompleted();

      if(queue.isEmpty()){
        server.shutdown();
      }

    }

    // @Override
    // public void requestResource(DataMessage msg, StreamObserver<DataMessage> rObserver) {
    //   // if (queue.getSize() == 0) return;
    //   LOG.info("Recieved matrix resource request");
    //   DataMessage response = DataMessage.newBuilder().build();
    //   try {
    //     if(N <= 512){
    //       response =
    //           DataMessage.newBuilder()
    //               .setA(A.toByteString())
    //               .setB(B.toByteString())
    //               .setIndex(p.getValue0())
    //               .build();
    //       rObserver.onNext(response);
    //     } else if (N > 512){
    //       ByteString ABytes = A.toByteString();
    //       ByteString BBytes = B.toByteString();
    //       response =
    //           DataMessage.newBuilder()
    //               .setA(ABytes.substring(0, ABytes.size()/2))
    //               .setB(BBytes.substring(0, BBytes.size()/2))
    //               .setIndex(p.getValue0())
    //               .build();
    //       rObserver.onNext(response);
          
    //       response =
    //           DataMessage.newBuilder()
    //               .setA(ABytes.substring(ABytes.size()/2))
    //               .setB(ABytes.substring(BBytes.size()/2))
    //               .setIndex(p.getValue0())
    //               .build();
    //       rObserver.onNext(response);
    //     }
    //   } catch (IOException e) {
    //     // TODO Auto-generated catch block
    //     LOG.error("Response built incorrectly" + e);
    //     e.printStackTrace();
    //     return;
    //   }
    //   LOG.info("Sent matrix response to worker");
    //   rObserver.onCompleted();
    // }

    // @Override
    // public void sendResult(DataMessage msg, StreamObserver<DataMessage> rObserver) {
    //   LOG.info("Recieved matrix result from worker");
    //   Matrix tempC;
    //   try {
    //     tempC = Matrix.fromByteString(msg.getA());
    //   } catch (ClassNotFoundException | IOException e) {
    //     // TODO Auto-generated catch block
    //     LOG.error("Unknown class type" + e);
    //     e.printStackTrace();
    //     return;
    //   }
      
    //   if (msg.getIndex() < 5) {
    //     // C1.incrementFromMatrixIndexes(tempC, MatrixIndexes.fromIndexes(msg.getIndexes()));
    //   } else if (msg.getIndex() < 9) {
    //     // C2.incrementFromMatrixIndexes(tempC, MatrixIndexes.fromIndexes(msg.getIndexes()));
    //   }
    //   DataMessage response = DataMessage.newBuilder().build();
    //   rObserver.onNext(response);
    //   rObserver.onCompleted();

    //   // queue.incrementCount();
    //   // if (queue.isDone()) {
    //   //   server.shutdown();
    //   // }
    // }
  }
}
