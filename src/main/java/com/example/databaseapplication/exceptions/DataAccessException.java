package com.example.databaseapplication.exceptions;

public class DataAccessException extends RuntimeException {


        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
        public DataAccessException(Throwable cause) {
            super(cause);
        }
}
