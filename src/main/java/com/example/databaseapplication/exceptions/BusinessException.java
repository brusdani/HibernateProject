
    package com.example.databaseapplication.exceptions;

    /**
     * Thrown when a business rule is violated, e.g.
     * when trying to create a character in a deleted world.
     */
    public class BusinessException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        /** No-arg constructor */
        public BusinessException() {
            super();
        }

        /**
         * @param message human-readable description of the business violation
         */
        public BusinessException(String message) {
            super(message);
        }

        /**
         * @param message human-readable description
         * @param cause original exception that triggered this
         */
        public BusinessException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * @param cause original exception that triggered this
         */
        public BusinessException(Throwable cause) {
            super(cause);
        }
    }

