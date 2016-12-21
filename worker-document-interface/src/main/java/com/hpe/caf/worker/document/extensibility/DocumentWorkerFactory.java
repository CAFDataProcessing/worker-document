package com.hpe.caf.worker.document.extensibility;

import com.hpe.caf.worker.document.model.Application;

/**
 * When a Document Worker is initialised it will first look for an implementation of this interface (using the standard Java service
 * loader). If one is available then the factory method that it provides will be used to construct the {@link DocumentWorker}
 * implementation.
 * <p>
 * The advantage of implementing this interface to construct the DocumentWorker implementation over simply registering the DocumentWorker
 * as a service provider is that the {@link Application} object is passed to the factory method, potentially allowing more advanced
 * initialisation choices to be made.
 */
public interface DocumentWorkerFactory
{
    /**
     * Constructs a new Document Worker. A Document Worker is passed documents to process and is able to add or remove fields from them.
     *
     * @param application an object which provides access to base functionality
     * @return the new Document Worker object
     */
    DocumentWorker createDocumentWorker(Application application);
}
