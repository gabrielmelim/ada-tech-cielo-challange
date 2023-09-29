package com.cielo.precadastroclientes.exception;

import java.util.List;

public class ClienteException extends RuntimeException {
    public ClienteException(String message) {
        super(message);
    }

    public static class ClienteExistenteException extends ClienteException {
        public ClienteExistenteException(String message) {
            super(message);
        }
    }

    public static class ClienteNaoCadastradoException extends ClienteException {
        public ClienteNaoCadastradoException(String message) {
            super(message);
        }
    }

}
