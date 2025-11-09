package com.conexa.swapi.shared.exception;

import com.conexa.swapi.shared.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura excepciones conocidas del dominio
     * - Usa el código y mensaje definidos en BaseException
     * - Retorna HTTP 200 porque la app decide el resultado a partir del "code"
     */
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse handleBaseException(BaseException ex) {
        return new BaseResponse(ex.getCode(), ex.getMessage());
    }

    /**
     * Captura cualquier error desconocido
     * - Loguea el stacktrace para debugging
     * - Marca code=9 y mensaje genérico
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse handleUnexpected(Exception ex) {
        ex.printStackTrace();
        return new BaseResponse(9, "error inesperado en el sistema, revisar en consola");
    }
}
