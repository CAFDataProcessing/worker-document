package com.hpe.caf.worker.document.model;

import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * Represents the collection of fields that are attached to a document. Each field has a name and a collection of data values.
 */
public interface Fields extends DocumentWorkerObject, Iterable<Field>
{
    /**
     * Gets a field object for the specified field. This can be used to read the data values currently associated with the field, or to
     * add or replace the data values.
     *
     * @param fieldName the name of the field to access
     * @return the object that can be used to access or update the field
     */
    @Nonnull
    Field get(String fieldName);

    /**
     * Returns the document that this collection of fields is associated with.
     *
     * @return the document that this collection of fields is associated with
     */
    @Nonnull
    Document getDocument();

    /**
     * Returns a sequential {@code Stream} with this field collection as its source.
     *
     * @return a sequential {@code Stream} over the collection of fields
     */
    @Nonnull
    Stream<Field> stream();
}
