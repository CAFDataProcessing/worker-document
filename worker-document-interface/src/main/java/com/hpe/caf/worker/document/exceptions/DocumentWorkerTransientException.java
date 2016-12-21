package com.hpe.caf.worker.document.exceptions;

/**
 * A DocumentWorkerTransientException indicates that a transient failure has occurred; the operation might be able to succeed if it is
 * retried at a later time.
 */
public class DocumentWorkerTransientException extends Exception
{
    /**
     * Constructs a DocumentWorkerTransientException with a given reason.
     *
     * @param message a description of the exception
     */
    public DocumentWorkerTransientException(String message)
    {
        super(message);
    }

    /**
     * Constructs a DocumentWorkerTransientException with a given reason and cause.
     *
     * @param message a description of the exception
     * @param cause the underlying reason for this exception
     */
    public DocumentWorkerTransientException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a DocumentWorkerTransientException with a given cause.
     *
     * @param cause the underlying reason for this exception
     */
    public DocumentWorkerTransientException(Throwable cause)
    {
        super(cause);
    }
}
