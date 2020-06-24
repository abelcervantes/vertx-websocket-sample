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
  public void start() {

    vertx.createHttpServer().webSocketHandler(ws -> {
      if (!ws.path().equals("/nolb")) {
        System.out.println("rejected");
        ws.reject();
        return;
      }

      System.out.println("nolb connected");

      ws.exceptionHandler(e -> {
        System.out.println("nolb exception " + e.getMessage());
      });

      ws.endHandler(end -> {
        System.out.println("nolb end handler");
      });

      ws.closeHandler(close -> {
        System.out.println("nolb close handler");
      });

    }).exceptionHandler(e -> {
      // only exceptions before the http connection is established...
      System.out.println("nolb exception " + e.getMessage());

    }).listen(5051);
  }

}
