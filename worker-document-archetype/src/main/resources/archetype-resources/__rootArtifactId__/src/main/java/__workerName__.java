#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.model.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an example implementation of the DocumentWorker interface.
 * <p>
 * Implementing the DocumentWorker interface provides an easy way to efficiently integrate into the Data Processing pipeline.
 * Documents passing through the pipeline can be routed to the worker and enriched from an external source such as a database.
 * <p>
 * The example implementation simply does a lookup from an internal in-memory map.
 * <p>
 * If it would be more efficient to process multiple documents together then the BulkDocumentWorker interface can be implemented instead
 * of the DocumentWorker interface.
 */
public class ${workerName} implements DocumentWorker
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
     * <p>
     * This example implementation sets the values of the 'UNIQUE_ID' field based on the values of the 'REFERENCE' field. The references
     * are looked up in an internal in-memory map, and if any of them are present then the corresponding unique ids are set.
     * <p>
     * Obviously a real implementation would likely query a central database rather than having an in-memory map, and it would also
     * operate in bulk rather than a single document at a time as presented here.
     *
     * @param document the document to be processed. Fields can be added or removed from the document.
     * @throws InterruptedException if any thread has interrupted the current thread
     * @throws DocumentWorkerTransientException if the document could not be processed
     */
    @Override
    public void processDocument(Document document) throws InterruptedException, DocumentWorkerTransientException
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
