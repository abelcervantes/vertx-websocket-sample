package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() throws UnknownHostException {
    ClusterManager mgr = new HazelcastClusterManager();

    String host = InetAddress.getLocalHost().getHostAddress();
    VertxOptions options = new VertxOptions()
            .setClusterManager(mgr)
            .setEventBusOptions(new EventBusOptions().setHost(host));

    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();

        HttpServerOptions opt = new HttpServerOptions().setLogActivity(true);
        vertx.createHttpServer(opt).webSocketHandler(ws -> {
          if (!ws.path().equals("/socket")) {
            System.out.println("rejected");
            ws.reject();
            return;
          }

          System.out.println("connected");

          /*vertx.setTimer(2000, t -> {
            //ws.reject(403);
            System.out.println("closing");
            ws.close();
          });*/

          vertx.eventBus().consumer("server.1", h -> {
            String body = (String) h.body();
            System.out.println("received: "+body);
          });

          ws.exceptionHandler(e -> {
            System.out.println("exception " + e.getMessage());
          });

          ws.endHandler(end -> {
            System.out.println("end handler");
          });

          ws.closeHandler(close -> {
            System.out.println("close handler");
          });

        }).exceptionHandler(e -> {
          // only exceptions before the http connection is established...
          System.out.println("exception " + e.getMessage());

        }).listen(5050);
      } else {
        System.out.println("clustered vertx failed");
      }
    });

  }

}
