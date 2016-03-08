package com.redhat.developer.msa.aloha;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class AlohaVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.get("/").handler(ctx -> {
			String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
			ctx.response()
					.putHeader("content-type", "text/plain")
					.end(String.format("Aloha mai %s", hostname));
		});
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

}
