package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.javatuples.Quartet;

public class Coordinator {
  private static final Logger LOG = Logger.getLogger(Coordinator.class);
  private static Properties p = new Properties();

  static {
    try {
      p.load(Coordinator.class.getClassLoader().getResourceAsStream("grpc.properties"));
    } catch (IOException e) {
      LOG.error(e);
    }
  }

  private static int N = Integer.parseInt(p.getProperty("N"));
  private static Matrix A = Matrix.createRandomMatrix(N, N);
  private static Matrix B = Matrix.I(N, N);
  private static Matrix C = new Matrix(N, N);
  private static Matrix C1 = new Matrix(N, N);
  private static Matrix C2 = new Matrix(N, N);
  private static ArrayBlockingQueue<Integer> workerQueue;
  private static CoordinatorQueue queue = new CoordinatorQueue();
  private static Server server;
  private static ArrayBlockingQueue<Integer> taskFinishedQueue = new ArrayBlockingQueue<Integer>(8);

  private void start() throws IOException {
    String propFileName = "grpc.properties";
    server =
        ServerBuilder.forPort(Integer.parseInt(p.getProperty("port")))
            .addService(new CoordinatorImpl())
            .build()
            .start();

    LOG.info("Coordinator started on " + p.getProperty("port"));
    LOG.info("Matrix size: " + p.getProperty("N") + " x " + p.getProperty("N"));
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
    public void discoverWorker(
        DiscoverRequest discoverRequest, StreamObserver<DiscoverResult> rObserver) {
      LOG.info("Recieved DiscoverRequest from Worker");
      DiscoverResult response =
          DiscoverResult.newBuilder().setWorkerId(queue.getCurrentWorkerId()).build();
      rObserver.onNext(response);
      LOG.info("Sent DiscoverResult response to Worker");
      rObserver.onCompleted();
    }

    @Override
    public void requestCompute(ControlMessage msg, StreamObserver<DataMessage> rObserver) {
      if (!(msg.getType() == ControlMessageType.AVAILABLE)) return;
      if (queue.isEmpty()) {
        try {
          DataMessage response = DataMessage.newBuilder().setIsWorkAvailable(false).build();
          rObserver.onNext(response);
        } catch (Exception e) {
          LOG.error("requestCompute response could not be sent");
          e.printStackTrace();
        }
        LOG.info("Empty Task Queue");
        rObserver.onCompleted();
        return;
      }

      LOG.info("Recieved matrix resource request from Worker " + msg.getWorkerId());
      Quartet<Matrix, Matrix, Matrix, Matrix> splitMatrixA = A.split4();
      Quartet<Matrix, Matrix, Matrix, Matrix> splitMatrixB = B.split4();
      Matrix A11 = splitMatrixA.getValue0();
      Matrix A12 = splitMatrixA.getValue1();
      Matrix A21 = splitMatrixA.getValue2();
      Matrix A22 = splitMatrixA.getValue3();

      Matrix B11 = splitMatrixB.getValue0();
      Matrix B12 = splitMatrixB.getValue1();
      Matrix B21 = splitMatrixB.getValue2();
      Matrix B22 = splitMatrixB.getValue3();

      Matrix tempA;
      Matrix tempB;

      int taskNum = queue.pop();
      if (taskNum == 1) {
        tempA = A11;
        tempB = B11;
      } else if (taskNum == 2) {
        tempA = A11;
        tempB = B12;
      } else if (taskNum == 3) {
        tempA = A21;
        tempB = B11;
      } else if (taskNum == 4) {
        tempA = A21;
        tempB = B12;
      } else if (taskNum == 5) {
        tempA = A12;
        tempB = B21;
      } else if (taskNum == 6) {
        tempA = A12;
        tempB = B22;
      } else if (taskNum == 7) {
        tempA = A22;
        tempB = B21;
      } else {
        tempA = A22;
        tempB = B22;
      }
      try {
        DataMessage response =
            DataMessage.newBuilder()
                .setA(tempA.toByteString())
                .setB(tempB.toByteString())
                .setIndex(taskNum)
                .setIsWorkAvailable(true)
                .build();
        rObserver.onNext(response);
      } catch (Exception e) {
        LOG.error("requestCompute response could not be sent");
        e.printStackTrace();
      }
      LOG.info("Sent matrix resource to Worker");
      rObserver.onCompleted();
    }

    @Override
    public void sendResult(ResultMessage msg, StreamObserver<ControlMessage> rObserver) {
      LOG.info("Recieved compute result from Worker " + msg.getWorkerId());
      if (msg.getType() == ResultMessageType.SUCCEED) {
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
        taskFinishedQueue.add(msg.getIndex());
        ControlMessage response = ControlMessage.newBuilder().build();
        rObserver.onNext(response);
        LOG.info("Sent success response back to Worker");
        rObserver.onCompleted();
        if (taskFinishedQueue.size() == 8) {
          server.shutdown();
        }
      } else {
        queue.push(msg.getIndex());
      }
    }
  }
}
