package com.hpe.caf.worker.document.model;

import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * Represents the collection of data values that belong to a field.
 */
public interface FieldValues extends DocumentWorkerObject, Iterable<FieldValue>
{
    /**
     * Returns the field that this collection of field values is associated with.
     *
     * @return the field that this collection of field values is associated with
     */
    @Nonnull
    Field getField();

    /**
     * Returns true if there are no field values in this collection.
     *
     * @return true if there are no field values in this collection
     */
    boolean isEmpty();

    /**
     * Returns the number of field values in this collection.
     *
     * @return the number of field values in this collection
     */
    int size();

    /**
     * Returns a sequential {@code Stream} with this field value collection as its source.
     *
     * @return a sequential {@code Stream} over the collection of data values
     */
    @Nonnull
    Stream<FieldValue> stream();
}
