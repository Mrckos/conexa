package com.conexa.swapi.shared.exception;


import lombok.Getter;

@Getter
public class BaseException extends Exception {

    private final Integer code;

    public BaseException() {
        super("Error inesperado en el sistema. Revisar en consola");
        this.code = 9;
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
