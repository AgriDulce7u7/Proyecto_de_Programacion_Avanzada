package com.uq.triage.exception;
public class ResponsableNotFoundException extends RuntimeException {
    public ResponsableNotFoundException(Long id) { super("No se encontró el responsable con ID: " + id); }
}
