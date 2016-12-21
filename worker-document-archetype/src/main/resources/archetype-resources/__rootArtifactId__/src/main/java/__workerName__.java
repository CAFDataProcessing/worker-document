#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.BulkDocumentWorker;
import com.hpe.caf.worker.document.model.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an example implementation of the BulkDocumentWorker interface.
 * <p>
 * Implementing the BulkDocumentWorker interface provides an easy way to efficiently integrate into the Data Processing pipeline.
 * Documents passing through the pipeline can be routed to the worker and enriched from an external source such as a database.
 * <p>
 * The example implementation simply does a lookup from an internal in-memory map.
 * <p>
 * If there is no efficiency to be gained by processing documents together then the DocumentWorker interface should be implemented instead
 * of the BulkDocumentWorker interface. (The example implementation breaks this rule but does so just for demonstration purposes.)
 */
public class ${workerName} implements BulkDocumentWorker
{
    private final Map<String, Integer> IdLookupMap;

    public ${workerName}()
    {
        IdLookupMap = new HashMap<>();
        IdLookupMap.put("/mnt/fs/docs/hr policy.doc", 1001);
        IdLookupMap.put("/mnt/fs/docs/Christmas party.doc", 1);
        IdLookupMap.put("/mnt/fs/docs/budget.doc", 291);
        IdLookupMap.put("/mnt/fs/docs/large orders.doc", 123);
        IdLookupMap.put("/mnt/fs/docs/strategy.doc", 45);
    }

    /**
     * This method provides an opportunity for the worker to report if it has any problems which would prevent it processing documents
     * correctly. If the worker is healthy then it should simply return without calling the health monitor.
     *
     * @param healthMonitor used to report the health of the application
     */
    @Override
    public void checkHealth(HealthMonitor healthMonitor)
    {
    }

    /**
     * Processes a single document.
     *
     * @param document the document to be processed. Fields can be added or removed from the document.
     * @throws InterruptedException if any thread has interrupted the current thread
     * @throws DocumentWorkerTransientException if the document could not be processed
     */
    @Override
    public void processDocument(Document document) throws InterruptedException, DocumentWorkerTransientException
    {
        doWorkOnFields(document);
    }

    /**
     * Processes a collection of documents.
     *
     * @param documents the documents to be processed. Fields can be added or removed from the documents.
     * @throws InterruptedException if any thread has interrupted the current thread
     * @throws DocumentWorkerTransientException if the documents could not be processed
     */
    @Override
    public void processDocuments(Documents documents) throws InterruptedException, DocumentWorkerTransientException
    {
        for (Document document : documents) {
            doWorkOnFields(document);
        }
    }

    /**
     * This example implementation sets the values of the 'UNIQUE_ID' field based on the values of the 'REFERENCE' field. The references
     * are looked up in an internal in-memory map, and if any of them are present then the corresponding unique ids are set.
     * <p>
     * Obviously a real implementation would likely query a central database rather than having an in-memory map, and it would also
     * operate in bulk rather than a single document at a time as presented here.
     *
     * @param document the document to be processed
     */
    private void doWorkOnFields(Document document)
    {
        // Get the REFERENCE and UNIQUE_ID fields
        Field referenceField = document.getField("REFERENCE");
        Field uniqueIdField = document.getField("UNIQUE_ID");

        // Clear any existing values from the UNIQUE_ID field
        uniqueIdField.clear();

        // Add new values to the UNIQUE_ID field by looking up the REFERENCE field values
        for (String reference : referenceField.getStringValues()) {
            Integer value = IdLookupMap.get(reference);

            if (value != null) {
                uniqueIdField.add(value.toString());
            }
        }
    }
}
