package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.xml.crypto.Data;

import com.google.protobuf.ByteString;

import org.apache.log4j.Logger;
import org.javatuples.Pair;
import org.javatuples.Quartet;

public class Coordinator {
  private static final Logger LOG = Logger.getLogger(Coordinator.class);

  private static int N = 512;
  private static Matrix A = Matrix.createRandomMatrix(N, N);
  private static Matrix B = Matrix.I(N, N);
  private static Matrix C = new Matrix(N, N);
  private static Matrix C1 = new Matrix(N, N);
  private static Matrix C2 = new Matrix(N, N);
  private static CoordinatorQueue queue = new CoordinatorQueue();
  private static Server server;

  private void start() throws IOException {
    int port = 8080;
    server = ServerBuilder.forPort(port).addService(new CoordinatorImpl()).build().start();

    LOG.info("Coordinator started on " + port);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread() {
              @Override
              public void run() {
                try {
                  Coordinator.this.stop();
                } catch (InterruptedException e) {
                  LOG.error("Thread no sucessfully started" + e);
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
      return;
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
