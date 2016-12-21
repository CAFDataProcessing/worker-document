package com.hpe.caf.worker.document;

import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.TaskFailedException;

public final class DocumentWorkerResultFunctions
{
    private DocumentWorkerResultFunctions()
    {
    }

    public static byte[] serialise(final DocumentWorkerResult result, final Codec codec)
    {
        try {
            return codec.serialise(result);
        } catch (CodecException e) {
            throw new TaskFailedException("Failed to serialise result", e);
        }
    }
}
