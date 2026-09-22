package rs.tiacgroup.fleetopsgateway.vehiclesearch.config;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestClientConfigTest {

    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void buildRestClient_shouldThrowResourceAccessExceptionWhenServerUnreachable() {
        // given
        RestClientConfig config = new RestClientConfig();
        ProviderProperties.ProviderConfig providerConfig =
                new ProviderProperties.ProviderConfig("http://localhost:9999", 100, 1000);
        ProviderProperties providerProperties = new ProviderProperties(providerConfig, providerConfig);

        RestClient restClient = config.freeProviderRestClient(providerProperties);

        // when / then
        assertThatThrownBy(() -> restClient.get().uri("/anything").retrieve().body(String.class))
                .isInstanceOf(ResourceAccessException.class);
    }

    @Test
    void buildRestClient_shouldThrowResourceAccessExceptionWhenReadTimeoutExceeded() throws IOException {
        // given
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/anything", exchange -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        });
        server.start();

        int port = server.getAddress().getPort();

        RestClientConfig config = new RestClientConfig();
        ProviderProperties.ProviderConfig providerConfig =
                new ProviderProperties.ProviderConfig("http://localhost:" + port, 1000, 100);
        ProviderProperties providerProperties = new ProviderProperties(providerConfig, providerConfig);

        RestClient restClient = config.freeProviderRestClient(providerProperties);

        // when / then
        assertThatThrownBy(() -> restClient.get().uri("/anything").retrieve().body(String.class))
                .isInstanceOf(ResourceAccessException.class);
    }

    @Test
    void buildRestClient_shouldThrowResourceAccessExceptionWhenConnectTimeoutExceeded() throws IOException {
        // given
        ServerSocket serverSocket = new ServerSocket(0, 1);
        int port = serverSocket.getLocalPort();

        try (Socket blockingConnection = new Socket("localhost", port)) {
            RestClientConfig config = new RestClientConfig();
            ProviderProperties.ProviderConfig providerConfig =
                    new ProviderProperties.ProviderConfig("http://localhost:" + port, 50, 1000);
            ProviderProperties providerProperties = new ProviderProperties(providerConfig, providerConfig);

            RestClient restClient = config.freeProviderRestClient(providerProperties);

            // when / then
            assertThatThrownBy(() -> restClient.get().uri("/anything").retrieve().body(String.class))
                    .isInstanceOf(ResourceAccessException.class);
        } finally {
            serverSocket.close();
        }
    }
}