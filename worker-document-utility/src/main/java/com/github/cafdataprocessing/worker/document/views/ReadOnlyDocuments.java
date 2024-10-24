/*
 * Copyright 2016-2024 Open Text.
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
package com.github.cafdataprocessing.worker.document.views;

import com.github.cafdataprocessing.worker.document.DocumentWorkerDocument;
import com.github.cafdataprocessing.worker.document.changelog.MutableDocument;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ReadOnlyDocuments
{
    private ReadOnlyDocuments()
    {
    }

    @Nonnull
    public static List<ReadOnlyDocument> create(final List<DocumentWorkerDocument> documents)
    {
        return (documents == null)
            ? Collections.emptyList()
            : Collections.unmodifiableList(documents.stream()
                .map(ReadOnlyDocument::create)
                .collect(Collectors.toList()));
    }

    @Nonnull
    public static List<ReadOnlyDocument> create(final ArrayList<MutableDocument> documents)
    {
        Objects.requireNonNull(documents);

        return Collections.unmodifiableList(documents
            .stream()
            .map(ReadOnlyDocument::create)
            .collect(Collectors.toList()));
    }

    @Nonnull
    public static List<ReadOnlyDocument> none()
    {
        return Collections.emptyList();
    }
}
