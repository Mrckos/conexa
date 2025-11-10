package com.conexa.swapi.conn.controller;

import com.conexa.swapi.conn.service.SwapiService;
import com.conexa.swapi.shared.exception.BaseException;
import com.conexa.swapi.shared.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(
        name = "SWAPI",
        description = "Consulta recursos a la API de Star Wars vía proxy interno. " +
                "Permite paginación, búsqueda por nombre e ID."
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SwapiController {

    private final SwapiService service;

    private static final Set<String> ALLOWED = Set.of("people", "films", "starships", "vehicles");

    @Operation(
            summary = "Consulta datos de SWAPI",
            description = """
                    Endpoint dinámico para consultar recursos de la Star Wars API.
                    
                    OPERACIÓN POR `id`:
                      • Si se especifica `id`, se recupera un solo recurso.
                    
                    OPERACIÓN LISTADO:
                      • Sin `id`, se retorna listado paginado
                      • Se puede filtrar por nombre

                    Recursos soportados:
                      • people
                      • films
                      • starships
                      • vehicles
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Recurso no encontrado",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
    )
    @GetMapping("/{resource}")
    public BaseResponse list(

            @Parameter(
                    description = "Recurso a consultar. Valores válidos: people, films, starships, vehicles",
                    example = "people"
            )
            @PathVariable String resource,

            @Parameter(
                    description = "Página solicitada (solo si NO se busca por id)",
                    example = "1"
            )
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(
                    description = "Cantidad de ítems por página",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") Integer size,

            @Parameter(
                    description = "Filtro opcional por nombre. Case-insensitive",
                    example = "luke"
            )
            @RequestParam(required = false) String name,

            @Parameter(
                    description = "ID del recurso. Si se envía, ignora paginación y filtrado.",
                    example = "1"
            )
            @RequestParam(required = false) String id

    ) throws BaseException {

        if (!ALLOWED.contains(resource)) {
            throw new BaseException(5, "Recurso inválido. Usa: people, films, starships, vehicles");
        }

        if (id != null && !id.isBlank()) {
            return service.getById(resource, id);
        }
        return service.list(resource, page, size, name);
    }
}
