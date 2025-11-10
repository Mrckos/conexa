package com.conexa.swapi.conn.service;

import com.conexa.swapi.conn.client.SwapiClient;
import com.conexa.swapi.conn.dto.*;
import com.conexa.swapi.shared.dto.BasePagResponse;
import com.conexa.swapi.shared.exception.BaseException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;
import java.util.*;
import static org.mockito.Mockito.*;

class SwapiServiceTest {

    //UNIT
    @Mock
    SwapiClient client;

    @InjectMocks
    SwapiService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // getById()
    @Test
    void getById_ok() throws BaseException {
        Map<String, Object> props = Map.of("name", "Luke Skywalker");
        SwapiGenericResult result = new SwapiGenericResult("1", null, null, props);
        SwapiGenericResponse resp = new SwapiGenericResponse("ok", result);

        when(client.getById("people", "1")).thenReturn(resp);

        var out = service.getById("people", "1");

        Assertions.assertEquals("1", out.getId());
        Assertions.assertEquals("Luke Skywalker", out.getName());
    }

    @Test
    void getById_notFound() throws BaseException{
        when(client.getById("people", "999")).thenReturn(null);

        BaseException ex = Assertions.assertThrows(
                BaseException.class,
                () -> service.getById("people", "999")
        );

        Assertions.assertEquals(4, ex.getCode());
        Assertions.assertEquals("No se encontró el ID", ex.getMessage());
    }

    // list normal
    @Test
    void list_ok() throws BaseException {
        List<SwapiItem> items = List.of(
                new SwapiItem("1", "Luke Skywalker", null),
                new SwapiItem("2", "Leia Organa", null)
        );

        SwapiListResponse resp = new SwapiListResponse("ok", 2, items);

        when(client.getList("people", 1, 10)).thenReturn(resp);

        BasePagResponse<SwapiDTO> out = service.list("people", 1, 10, null);

        Assertions.assertEquals(0, out.getCode());
        Assertions.assertEquals(2, out.getTotal());
        Assertions.assertEquals(2, out.getItems().size());
        Assertions.assertEquals("1", out.getItems().get(0).getId());
    }

    @Test
    void list_noResults() throws BaseException{
        SwapiListResponse resp = new SwapiListResponse("ok", null, null);
        when(client.getList("people", 1, 10)).thenReturn(resp);

        BaseException ex = Assertions.assertThrows(
                BaseException.class,
                () -> service.list("people", 1, 10, null)
        );

        Assertions.assertEquals(6, ex.getCode());
        Assertions.assertEquals("El recurso no soporta paginación o no hay resultados", ex.getMessage());
    }

    //INTEGRACION

    static MockWebServer mockServer;
    SwapiService integrationService;
    SwapiClient integrationClient;

    @BeforeAll
    static void startServer() throws Exception {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        mockServer.shutdown();
    }

    @BeforeEach
    void setupIntegration() {
        String baseUrl = mockServer.url("/").toString();

        WebClient w = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        integrationClient = new SwapiClient(w);
        integrationService = new SwapiService(integrationClient);
    }

    private void stubFilm(String id, String title) {
        mockServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody("""
                {
                  "message": "ok",
                  "result": {
                    "uid": "%s",
                    "name": "%s",
                    "description": "descripcion",
                    "properties": {
                      "title": "%s"
                    }
                  }
                }
                """.formatted(id, title, title))
        );
    }

    @Test
    void films_listPaged_ok() throws BaseException {

        stubFilm("1", "A New Hope");
        stubFilm("2", "Empire Strikes Back");
        stubFilm("3", "Return of the Jedi");
        stubFilm("4", "The Phantom Menace");
        stubFilm("5", "Attack of the Clones");
        stubFilm("6", "Revenge of the Sith");
        stubFilm("7", "The Force Awakens");

        BasePagResponse<SwapiDTO> out =
                integrationService.list("films", 1, 3, null);
        System.out.println("Requests recibidos: " + mockServer.getRequestCount());

        Assertions.assertEquals(0, out.getCode());
        Assertions.assertEquals(7, out.getTotal());
        Assertions.assertEquals(3, out.getItems().size());
        Assertions.assertEquals("1", out.getItems().get(0).getId());
        Assertions.assertEquals("A New Hope", out.getItems().get(0).getName());
    }

    @Test
    void films_filter_ok() throws BaseException, NoSuchFieldException, IllegalAccessException {

        // Forzar cache manual
        List<SwapiDTO> cache = List.of(
                new SwapiDTO("1", "A New Hope"),
                new SwapiDTO("2", "Empire Strikes Back")
        );

        // inyecta cache
        Field field = SwapiService.class.getDeclaredField("filmsCache");
        field.setAccessible(true);
        field.set(integrationService, cache);

        BasePagResponse<SwapiDTO> out =
                integrationService.list("films", 1, 10, "hope");

        Assertions.assertEquals(1, out.getItems().size());
        Assertions.assertEquals("A New Hope", out.getItems().get(0).getName());
    }
    @Test
    void films_pageOutOfRange() throws Exception {

        //cache manual
        List<SwapiDTO> cache = List.of(
                new SwapiDTO("1", "A New Hope")
        );

        // inyectamos
        var field = SwapiService.class.getDeclaredField("filmsCache");
        field.setAccessible(true);
        field.set(integrationService, cache);

        BaseException ex = Assertions.assertThrows(
                BaseException.class,
                () -> integrationService.list("films", 10, 10, null)
        );

        Assertions.assertEquals(7, ex.getCode());
        Assertions.assertEquals("La página solicitada está fuera de rango", ex.getMessage());
    }
}
