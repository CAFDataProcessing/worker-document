/*
 * Copyright 2018-2017 EntIT Software LLC, a Micro Focus company.
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
package com.hpe.caf.worker.document.views;

import com.hpe.caf.worker.document.DocumentWorkerFailure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class ReadOnlyFailures
{
    private ReadOnlyFailures()
    {
    }

    @Nonnull
    public static List<ReadOnlyFailure> create(final List<DocumentWorkerFailure> failures)
    {
        return (failures == null)
            ? Collections.emptyList()
            : Collections.unmodifiableList(createStream(failures).collect(Collectors.toList()));
    }

    @Nonnull
    public static Stream<ReadOnlyFailure> createStream(final List<DocumentWorkerFailure> failures)
    {
        return failures.stream()
            .map(ReadOnlyFailure::create)
            .filter(Objects::nonNull);
    }

    @Nonnull
    public static List<ReadOnlyFailure> create(final ArrayList<ReadOnlyFailure> failures)
    {
        return Collections.unmodifiableList(failures);
    }

    @Nonnull
    public static List<ReadOnlyFailure> none()
    {
        return Collections.emptyList();
    }
}
