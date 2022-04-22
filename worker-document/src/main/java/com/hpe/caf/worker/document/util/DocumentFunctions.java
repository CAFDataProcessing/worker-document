/*
 * Copyright 2016-2022 Micro Focus or one of its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hpe.caf.worker.document.util;

import com.hpe.caf.worker.document.model.Document;
import java.util.stream.Stream;

/**
 * Document-related utility functions.
 */
public final class DocumentFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private DocumentFunctions()
    {
    }

    /**
     * Returns a {@code Stream} of documents containing the specified document and all of its subdocuments and their subdocuments
     * recursively.
     *
     * @param document the root document of the hierarchy
     * @return a {@code Stream} of the documents that are in the specified document's hierarchy
     */
    public static Stream<Document> documentNodes(final Document document)
    {
        return Stream.concat(
            Stream.of(document),
            document.getSubdocuments().stream().flatMap(DocumentFunctions::documentNodes));
    }
}
