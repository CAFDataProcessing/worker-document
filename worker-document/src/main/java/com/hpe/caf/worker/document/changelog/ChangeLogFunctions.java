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
package com.hpe.caf.worker.document.changelog;

import com.hpe.caf.worker.document.DocumentWorkerChange;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerFailure;
import java.util.List;

/**
 * Utility functions related to inspecting the change log.
 */
public final class ChangeLogFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private ChangeLogFunctions()
    {
    }

    /**
     * Checks whether the specified changes collectively add up to any new failures to the document or any of its subdocuments.
     *
     * @param changes the changes to be inspected
     * @return true if the changes would cause new failures to be applied
     */
    public static boolean hasFailures(final List<DocumentWorkerChange> changes)
    {
        return (changes != null)
            && (hasDirectFailures(changes) || hasSubdocumentFailures(changes));
    }

    private static boolean hasDirectFailures(final List<DocumentWorkerChange> changes)
    {
        boolean hasFailures = false;

        for (final DocumentWorkerChange change : changes) {
            if (change.addFailure != null) {
                hasFailures = true;
            }

            if (change.setFailures != null) {
                hasFailures = !change.setFailures.isEmpty();
            }
        }

        return hasFailures;
    }

    private static boolean hasSubdocumentFailures(final List<DocumentWorkerChange> changes)
    {
        // For simplicity sake I'm not taking into account the possibility that a subdocument which has been added or updated with
        // failures later has those failures removed by a further update or removal of the subdocument.
        return changes.stream().anyMatch(change
            -> ((change.addSubdocument != null && hasFailures(change.addSubdocument))
            || (change.insertSubdocument != null && hasFailures(change.insertSubdocument.subdocument))
            || (change.updateSubdocument != null && hasFailures(change.updateSubdocument.changes))));
    }

    private static boolean hasFailures(final DocumentWorkerDocument document)
    {
        // Check that the document is not null
        if (document == null) {
            return false;
        }

        // Check if it has any failures
        final List<DocumentWorkerFailure> failures = document.failures;

        if (failures != null && !failures.isEmpty()) {
            return true;
        }

        // Recursively check its subdocuments for failures
        final List<DocumentWorkerDocument> subdocuments = document.subdocuments;

        return subdocuments != null
            && subdocuments.stream().anyMatch(ChangeLogFunctions::hasFailures);
    }
}
