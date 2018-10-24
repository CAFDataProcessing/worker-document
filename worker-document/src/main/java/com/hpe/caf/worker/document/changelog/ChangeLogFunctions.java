/*
 * Copyright 2016-2018 Micro Focus or one of its affiliates.
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

import com.google.common.base.Joiner;
import com.hpe.caf.worker.document.DocumentWorkerChange;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerFailure;
import java.util.List;

/**
 * Utility functions related to inspecting the change log.
 */
public final class ChangeLogFunctions
{
    private static String failureMsg;

    private static String getFailureMsgs() {
        return failureMsg;
    }

    private static void setFailureMsgs(String failureMsgs) {
        ChangeLogFunctions.failureMsg = Joiner.on(",").skipNulls().join(ChangeLogFunctions.failureMsg,failureMsgs);
    }

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

    public static String getAllFailureMsgs(final List<DocumentWorkerChange> changes)
    {
        if(changes!=null){
            for (final DocumentWorkerChange change : changes) {
                if (change.addFailure != null) {
                    setFailureMsgs(change.addFailure.failureMessage);
                }
                if (change.setFailures != null) {
                    change.setFailures.stream().forEach(documentWorkerFailure -> setFailureMsgs(documentWorkerFailure
                            .failureMessage));
                }
                if(change.addSubdocument!=null){
                    getAllFailureMsgs(change.addSubdocument);
                }
                if(change.insertSubdocument!=null){
                    getAllFailureMsgs(change.insertSubdocument.subdocument);
                }
                if(change.updateSubdocument!=null){
                    getAllFailureMsgs(change.updateSubdocument.changes);
                }
            }
        }
        return getFailureMsgs();
    }
    private static void getAllFailureMsgs (final DocumentWorkerDocument document){
        if (document!=null){
            // Check if it has any failures
            final List<DocumentWorkerFailure> failures = document.failures;

            if (failures != null && !failures.isEmpty()) {
                failures.stream().forEach(documentWorkerFailure -> setFailureMsgs(documentWorkerFailure.failureMessage));
            }

            // Recursively check its subdocuments for failures
            final List<DocumentWorkerDocument> subdocuments = document.subdocuments;
            if(subdocuments!=null){
                subdocuments.stream().forEach(ChangeLogFunctions::getAllFailureMsgs);
            }
        }
    }
}
