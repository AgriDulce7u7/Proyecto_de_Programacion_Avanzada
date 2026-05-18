package com.uq.triage.exception;
public class ResponsableInactivoException extends RuntimeException {
    public ResponsableInactivoException(Long id) {
        super("El responsable con ID " + id + " no está activo y no puede ser asignado.");
    }
}
