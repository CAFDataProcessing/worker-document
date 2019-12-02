/*
 * Copyright 2016-2020 Micro Focus or one of its affiliates.
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

import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class ReadOnlyFieldValues
{
    private ReadOnlyFieldValues()
    {
    }

    @Nonnull
    public static List<ReadOnlyFieldValue> create(final List<DocumentWorkerFieldValue> fieldValues)
    {
        return (fieldValues == null)
            ? Collections.emptyList()
            : Collections.unmodifiableList(
                createStream(fieldValues).collect(Collectors.toList()));
    }

    @Nonnull
    public static Stream<ReadOnlyFieldValue> createStream(final List<DocumentWorkerFieldValue> fieldValues)
    {
        return fieldValues.stream()
            //.filter(Objects::nonNull) --> Leaving this out to purposely cause a NullPointerException
            // (to avoid changing the behaviour, although perhaps I should reconsider and just go ahead and change the behaviour for this)
            .map(ReadOnlyFieldValue::create);
    }

    @Nonnull
    public static List<ReadOnlyFieldValue> create(final ArrayList<ReadOnlyFieldValue> fieldValues)
    {
        return Collections.unmodifiableList(fieldValues);
    }
}
