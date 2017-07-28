package com.cmarcksthespot.budget

import io.netty.buffer.ByteBuf
import io.reactivex.netty.protocol.http.server.file.WebappFileRequestHandler
import io.reactivex.netty.protocol.http.server.{ HttpServerRequest, HttpServerResponse, RequestHandler }
import rx.Observable

class WebappHandler(handler: RequestHandler[ByteBuf, ByteBuf]) extends RequestHandler[ByteBuf, ByteBuf] {
  val underlying = new WebappFileRequestHandler()
  override def handle(request: HttpServerRequest[ByteBuf], response: HttpServerResponse[ByteBuf]): Observable[Void] = {
    if (request.getPath.startsWith("/app/")) {
      underlying.handle(request, response)
    } else handler.handle(request, response)
  }
}
