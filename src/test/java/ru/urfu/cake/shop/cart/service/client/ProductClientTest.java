package ru.urfu.cake.shop.cart.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import ru.urfu.cake.shop.cart.service.client.Response.BaseProductResponse;
import ru.urfu.cake.shop.cart.service.client.dto.ProductPriceDto;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductClient")
class ProductClientTest {

    private MockWebServer mockWebServer;
    private ProductClient productClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockWebServer = new MockWebServer();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        productClient = new ProductClient(webClient);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("должен вернуть цену, когда сервис продуктов возвращает успешный ответ с данными")
    void getPrice_shouldReturnPrice_whenResponseIsSuccessful() throws Exception {
        BaseProductResponse<ProductPriceDto> response = new BaseProductResponse<>();
        response.setSuccess(true);

        ProductPriceDto priceDto = new ProductPriceDto();
        priceDto.setPrice(BigDecimal.valueOf(599.99));
        response.setData(priceDto);

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(response))
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200));

        BigDecimal price = productClient.getPrice(UUID.randomUUID());

        assertThat(price)
                .as("Цена должна соответствовать значению из ответа")
                .isEqualByComparingTo(BigDecimal.valueOf(599.99));
    }

    @Test
    @DisplayName("должен вернуть ноль, когда сервис продуктов возвращает success=false")
    void getPrice_shouldReturnZero_whenResponseIsNotSuccess() throws Exception {
        BaseProductResponse<ProductPriceDto> response = new BaseProductResponse<>();
        response.setSuccess(false);

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(response))
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200));

        BigDecimal price = productClient.getPrice(UUID.randomUUID());

        assertThat(price)
                .as("Цена должна быть ZERO при success=false")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("должен вернуть ноль, когда сервис продуктов возвращает data=null")
    void getPrice_shouldReturnZero_whenDataIsNull() throws Exception {
        BaseProductResponse<ProductPriceDto> response = new BaseProductResponse<>();
        response.setSuccess(true);
        response.setData(null);

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(response))
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200));

        BigDecimal price = productClient.getPrice(UUID.randomUUID());

        assertThat(price)
                .as("Цена должна быть ZERO при data=null")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("должен вернуть ноль, когда сервис продуктов возвращает пустое тело ответа")
    void getPrice_shouldReturnZero_whenBodyIsEmpty() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("")
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200));

        BigDecimal price = productClient.getPrice(UUID.randomUUID());

        assertThat(price)
                .as("Цена должна быть ZERO при пустом теле ответа")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("должен вернуть ноль, когда сервис продуктов возвращает 500")
    void getPrice_shouldReturnZero_whenServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500));

        BigDecimal price = productClient.getPrice(UUID.randomUUID());

        assertThat(price)
                .as("Цена должна быть ZERO при ошибке 500")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("должен вернуть ноль, когда сервис продуктов возвращает 404")
    void getPrice_shouldReturnZero_whenClientError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        BigDecimal price = productClient.getPrice(UUID.randomUUID());

        assertThat(price)
                .as("Цена должна быть ZERO при ошибке 404")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("должен отправлять GET-запрос на правильный URL")
    void getPrice_shouldMakeRequestToCorrectUrl() throws Exception {
        BaseProductResponse<ProductPriceDto> response = new BaseProductResponse<>();
        response.setSuccess(true);

        ProductPriceDto priceDto = new ProductPriceDto();
        priceDto.setPrice(BigDecimal.valueOf(100));
        response.setData(priceDto);

        UUID variantId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(response))
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200));

        productClient.getPrice(variantId);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod())
                .as("Метод запроса должен быть GET")
                .isEqualTo("GET");
        assertThat(recordedRequest.getPath())
                .as("URL должен содержать ID варианта продукта")
                .isEqualTo("/product/variant/" + variantId);
    }

    @Test
    @DisplayName("должен обрабатывать недоступность сервиса продуктов без выброса исключения")
    void getPrice_shouldReturnZero_whenServiceIsUnavailable() {
        WebClient failingClient = WebClient.builder()
                .baseUrl("http://localhost:1")
                .build();
        ProductClient failingProductClient = new ProductClient(failingClient);

        BigDecimal price = failingProductClient.getPrice(UUID.randomUUID());

        assertThat(price)
                .as("Цена должна быть ZERO при недоступном сервисе")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }
}
