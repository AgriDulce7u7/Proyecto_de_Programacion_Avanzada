package com.uq.triage.exception;
public class SolicitudNotFoundException extends RuntimeException {
    public SolicitudNotFoundException(Long id) { super("No se encontró la solicitud con ID: " + id); }
}
