package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.Matrix.MatrixIndexes;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.javatuples.Pair;

public class Coordinator {
  private static final Logger LOG = Logger.getLogger(Coordinator.class);

  private static int N = 16;
  private static Matrix A = Matrix.createRandomMatrix(N, N);
  private static Matrix B = Matrix.I(N, N);
  private static Matrix C = new Matrix(N, N);
  private static Matrix C1 = new Matrix(N, N);
  private static Matrix C2 = new Matrix(N, N);
  private static CoordinatorQueue queue = new CoordinatorQueue(N);
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
                  LOG.error(e);
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

    C.incrementFromMatrices(C1, C2);
    LOG.info(C.toString());
    LOG.info(A.equals(C));
  }

  static class CoordinatorImpl extends CoordinatorGrpc.CoordinatorImplBase {
    @Override
    public void requestResource(DataMessage msg, StreamObserver<DataMessage> rObserver) {
      if (!msg.getType().equals(DataMessageType.SEND_REQUEST)) return;
      if (queue.getSize() == 0) return;
      Pair<Integer, MatrixIndexes> p = queue.poll();
      DataMessage response;
      try {
        response =
            DataMessage.newBuilder()
                .setType(DataMessageType.REPLY_REQUEST)
                .setA(A.toByteString())
                .setB(B.toByteString())
                .setIndexes(p.getValue1().toIndexes())
                .setIndex(p.getValue0())
                .build();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return;
      }
      rObserver.onNext(response);
      rObserver.onCompleted();
    }

    @Override
    public void sendResult(DataMessage msg, StreamObserver<DataMessage> rObserver) {
      if (!msg.getType().equals(DataMessageType.SEND_RESULT)) return;
      Matrix tempC;
      try {
        tempC = Matrix.fromByteString(msg.getA());
      } catch (ClassNotFoundException | IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return;
      }
      LOG.info(msg.getIndexes());
      LOG.info(tempC.toString());
      if (msg.getIndex() < 5) {
        C1.incrementFromMatrixIndexes(tempC, MatrixIndexes.fromIndexes(msg.getIndexes()));
      } else if (msg.getIndex() < 9) {
        C2.incrementFromMatrixIndexes(tempC, MatrixIndexes.fromIndexes(msg.getIndexes()));
      }
      DataMessage response = DataMessage.newBuilder().setType(DataMessageType.REPLY_RESULT).build();
      rObserver.onNext(response);
      rObserver.onCompleted();

      queue.incrementCount();
      LOG.info(queue.getCount());
      if (queue.isDone()) {
        server.shutdown();
      }
    }
  }
}
