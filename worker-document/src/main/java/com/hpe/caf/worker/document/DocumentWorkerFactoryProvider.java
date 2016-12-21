package com.hpe.caf.worker.document;

import com.hpe.caf.worker.document.extensibility.DocumentWorkerFactory;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.extensibility.BulkDocumentWorker;
import com.hpe.caf.worker.document.model.Application;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.ConfigurationSource;
import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.WorkerException;
import com.hpe.caf.api.worker.WorkerFactory;
import com.hpe.caf.api.worker.WorkerFactoryProvider;
import com.hpe.caf.worker.document.impl.ApplicationImpl;

/**
 * This is the main entry-point class for this module.
 * <p>
 * The worker-core module uses the Java service provider to locate and instantiate this class.
 * <p>
 * This module in turn uses the Java service provider to locate the actual implementation and then constructs the appropriate adapter
 * depending on whether or not the implementation supports bulk processing.
 */
public final class DocumentWorkerFactoryProvider implements WorkerFactoryProvider
{
    @Override
    public WorkerFactory getWorkerFactory(final ConfigurationSource configSource, final DataStore dataStore, final Codec codec)
        throws WorkerException
    {
        // Construct the application object
        final ApplicationImpl application = new ApplicationImpl(configSource, dataStore, codec);

        // Construct the DocumentWorker implementation object
        final DocumentWorker documentWorker = createDocumentWorker(application);

        if (documentWorker == null) {
            throw new WorkerException("The DocumentWorker instance could not be constructed. "
                + "Check that the implementation JARs are on the classpath.");
        }

        // Construct the appropriate type of adapter depending on whether the implementation support processing documents in bulk
        if (documentWorker instanceof BulkDocumentWorker) {
            return new BulkDocumentWorkerAdapter(application, (BulkDocumentWorker) documentWorker);
        } else {
            return new DocumentWorkerAdapter(application, documentWorker);
        }
    }

    /**
     * This function constructs the implementation of the DocumentWorker interface. It uses the standard Java service provider. If there
     * is a DocumentWorkerFactory registered then it will be loaded and used to construct the DocumentWorker object. If not then the
     * DocumentWorker will be loaded directly.
     *
     * @return the new DocumentWorker object, or null if it could not be constructed
     */
    private static DocumentWorker createDocumentWorker(final Application application)
    {
        final DocumentWorkerFactory documentWorkerFactory = ServiceFunctions.loadService(DocumentWorkerFactory.class);

        if (documentWorkerFactory == null) {
            return ServiceFunctions.loadService(DocumentWorker.class);
        } else {
            return documentWorkerFactory.createDocumentWorker(application);
        }
    }
}
