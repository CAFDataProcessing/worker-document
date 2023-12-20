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
package com.hpe.caf.worker.document.util;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions related to list collections.
 */
public final class ListFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private ListFunctions()
    {
    }

    /**
     * Returns a copy of the specified list and leaves the specified extra capacity.
     *
     * @param <T> the type of elements
     * @param list the list to be copied
     * @param extraCapacity the amount of space capacity required
     * @return a copy of the specified list with the required extra capacity
     */
    @Nonnull
    public static <T> ArrayList<T> copy(final List<T> list, final int extraCapacity)
    {
        if (list == null) {
            return new ArrayList<>(extraCapacity);
        } else {
            final ArrayList<T> returnList = new ArrayList<>(list.size() + extraCapacity);
            returnList.addAll(list);

            return returnList;
        }
    }

    /**
     * Returns the specified list if is contains any elements, or {@code null} if the list is empty.
     *
     * @param <T> the type of elements
     * @param list the list to be checked
     * @return the specified list or null if it is empty
     */
    public static <T> List<T> emptyToNull(final List<T> list)
    {
        return isNullOrEmpty(list) ? null : list;
    }

    /**
     * Returns true if the specified list is {@code null} or contains no elements.
     *
     * @param <T> the type of elements
     * @param list the list to be checked
     * @return true if the list if null or contains no elements
     */
    public static <T> boolean isNullOrEmpty(final List<T> list)
    {
        return list == null || list.isEmpty();
    }
}
