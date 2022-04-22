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
package com.hpe.caf.worker.document.views;

import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class ReadOnlyFieldValue
{
    private final DocumentWorkerFieldValue fieldValue;

    @Nonnull
    public static ReadOnlyFieldValue create(final DocumentWorkerFieldValue fieldValue)
    {
        return new ReadOnlyFieldValue(fieldValue);
    }

    private ReadOnlyFieldValue(final DocumentWorkerFieldValue fieldValue)
    {
        this.fieldValue = Objects.requireNonNull(fieldValue);
    }

    @Nonnull
    public String getData()
    {
        return nullToEmpty(fieldValue.data);
    }

    @Nonnull
    public DocumentWorkerFieldEncoding getEncoding()
    {
        return nullToUtf8(fieldValue.encoding);
    }

    /**
     * Returns the given string if it is non-null; the empty string otherwise.
     *
     * @param str the string to test and possibly return
     * @return string itself if it is non-null; "" if it is null
     */
    @Nonnull
    private static String nullToEmpty(final String str)
    {
        return (str != null) ? str : "";
    }

    /**
     * Returns the given encoding if it is non-null; UTF-8 otherwise.
     *
     * @param encoding the encoding to test and possibly return
     * @return encoding itself if it is non-null; utf8 if it is null
     */
    @Nonnull
    private static DocumentWorkerFieldEncoding nullToUtf8(final DocumentWorkerFieldEncoding encoding)
    {
        return (encoding != null) ? encoding : DocumentWorkerFieldEncoding.utf8;
    }
}
