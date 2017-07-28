package com.cmarcksthespot.budget

import com.cmarcksthespot.budget.api.{DefaultApiImpl, DefaultApiRouter}
import com.cmarcksthespot.budget.db.Setup
import com.netflix.hystrix.contrib.rxnetty.metricsstream.HystrixMetricsStreamHandler
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.netty.RxNetty
import io.reactivex.netty.protocol.http.server.{ErrorResponseGenerator, HttpServer, HttpServerResponse, RequestHandler}
import slick.driver.MySQLDriver.api.Database

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {

  def appSetup() = {
    val db = Database.forConfig("db.default")
    Await.result(Setup(db).createTables(), Duration.Inf)

    (DefaultApiRouter.createService(new DefaultApiImpl()), { () => db.close() })
  }

  final def main(args: Array[String]): Unit = {
    val (service, onShutdown) = appSetup()

    /* Initialize the application and server. */
    println("Server is starting up")
    val server = mkServer(9000, service.handler)
    server.start
    println("Server started")

    /* Construct a sane shutdown procedure for the server and service. */
    val doShutdown: () => Unit = () => {
      println("Application is shutting down")
      onShutdown()
      println("Server is shutting down")
      server.shutdown()
      /* We call this here to make sure the server has terminated (no new
         connections and all active have terminated) before shutting down the
         services. */
      server.waitTillShutdown()
      println("Shutdown complete")
    }


    if (Thread.currentThread().getName.startsWith("run-main-")) {
      /* We're running under SBT so stay attached to the terminal and shutdown
         on key-press. */
      System.out.println(s"Server running on port ${server.getServerPort}. Press enter to terminate.")
      System.in.read()
      doShutdown()
    } else {
      /* Not running under SBT, or we are but under a forked JVM. In either
         case we ensure shutdown but registering our shutdown procedure as a
         shutdown hook. */
      sys.addShutdownHook {
        doShutdown()
      }
    }

    /* Block the main thread to prevent exit until the server has shutdown. */
    server.waitTillShutdown()
    println("fully shut down")
  }

  def mkServer(port: Int, handler: RequestHandler[ByteBuf, ByteBuf]) = {
    val webappHandler = new WebappHandler(handler)

    /* Wrap the handler in a HystrixMetricsStreamHandler to give us Hystrix
       metrics reporting. */
    val metricsHandler = new HystrixMetricsStreamHandler(webappHandler)
    val loggingHandler = new RequestLoggingHandler(metricsHandler)

    val server: HttpServer[ByteBuf, ByteBuf] =
      RxNetty.createHttpServer(port, loggingHandler)
        .withErrorResponseGenerator(
          new ErrorResponseGenerator[ByteBuf] {
            override def updateResponse(response: HttpServerResponse[ByteBuf], error: Throwable): Unit = {
              println("Unhandled exception", error)
              response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
            }
          }
        )

    server
  }
}
