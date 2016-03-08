package com.redhat.developer.msa.aloha;

import io.vertx.core.Vertx;

public class AlohaApplication {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new AlohaVerticle());
	}

}
