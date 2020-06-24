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

              vertx.createHttpServer()
                      .requestHandler(req -> {
                          vertx.eventBus().publish("server.1", "Hello from Core!");
                          req.response().end();
                      }).listen(6060);
          } else {
              System.out.println("clustered vertx failed");
          }
      });
  }

}
