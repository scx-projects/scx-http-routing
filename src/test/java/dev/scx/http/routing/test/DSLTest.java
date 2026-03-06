package dev.scx.http.routing.test;

import dev.scx.function.Function1Void;
import dev.scx.http.ScxHttpServer;
import dev.scx.http.ScxHttpServerRequest;
import dev.scx.http.error_handler.ScxHttpServerErrorHandler;
import dev.scx.http.exception.UnauthorizedException;
import dev.scx.http.routing.Router;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


public class DSLTest {

    public static void main(String[] args) throws IOException {
        test1();
    }

    /// dsl 写法流畅度测试
    @Test
    public static void test1() throws IOException {

        var router = Router.of();

        router.route("/abc", c -> {
            var multiPart = c.request().asMultiPart();
            try (multiPart) {
                for (var part : multiPart) {
                    System.out.println(part.name() + " " + part.asBytes().length);
                }
            }
            c.next();
        });

        router.route(-1, "/*", c -> {
            System.out.println(c.pathMatch().capture("*"));
            c.next();
        });

        router.get("/hello", c -> {
            c.request().response().send("hello");
        });

        router.get("/path-params/:id", c -> {
            c.request().response().send("id : " + c.pathMatch().capture("id"));
        });

        router.get("/401", c -> {
            throw new UnauthorizedException();
        });

        router.post("/405", c -> {
            System.out.println("405");
        });

        router.get("/last", c -> {
            var r = 1 / 0;
        });

        var httpServer = new TestHttpServer();

        httpServer.onRequest(router);

    }

    static class TestHttpServer implements ScxHttpServer {

        @Override
        public ScxHttpServer onRequest(Function1Void<ScxHttpServerRequest, ?> requestHandler) {
            return null;
        }

        @Override
        public ScxHttpServer onError(ScxHttpServerErrorHandler errorHandler) {
            return null;
        }

        @Override
        public void start(SocketAddress localAddress) throws IOException {

        }

        @Override
        public void stop() {

        }

        @Override
        public InetSocketAddress localAddress() {
            return null;
        }

    }

}
