/**
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.developers.msa.aloha;

import java.util.ArrayList;
import java.util.List;

import com.github.kennedyoliveira.hystrix.contrib.vertx.metricsstream.EventMetricsStreamHandler;
import com.netflix.config.ConfigurationManager;

import feign.Logger;
import feign.Logger.Level;
import feign.hystrix.HystrixFeign;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class AlohaVerticle extends AbstractVerticle {

    /**
     * The next REST endpoint URL of the service chain to be called.
     */
    private static final String NEXT_ENDPOINT_URL = "http://bonjour:8080/";

    /**
     * Setting Hystrix timeout for the chain in 250ms (we only have 1 service to call).
     */
    static {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 250);
    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/api/aloha").handler(ctx -> {
            addCORSHeaders(ctx.response())
                .end(aloha());
        });
        router.get("/api/aloha-chaining").handler(ctx -> {
            addCORSHeaders(ctx.response())
                .putHeader("Content-Type", "application/json")
                .end(Json.encode(alohaChaining()));
        });
        router.get(EventMetricsStreamHandler.DEFAULT_HYSTRIX_PREFIX)
            .handler(EventMetricsStreamHandler.createHandler());

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        System.out.println("Service running at 0.0.0.0:8080");
    }

    private String aloha() {
        String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
        return String.format("Aloha mai %s", hostname);
    }

    private List<String> alohaChaining() {
        List<String> greetings = new ArrayList<>();
        greetings.add(aloha());
        greetings.add(getNextService().bonjour());
        return greetings;
    }

    /**
     * This is were the "magic" happens: it creates a Feign, which is a proxy interface for remote calling a REST endpoint with
     * Hystrix fallback support.
     *
     * @return The feign pointing to the service URL and with Hystrix fallback.
     */
    private BonjourService getNextService() {
        return HystrixFeign.builder()
            .logger(new Logger.ErrorLogger()).logLevel(Level.BASIC)
            .target(BonjourService.class, NEXT_ENDPOINT_URL,
                () -> "Bonjour response (fallback)");
    }

    private HttpServerResponse addCORSHeaders(HttpServerResponse response) {
        return response.putHeader("Access-Control-Allow-Origin", "*");
    }

}
