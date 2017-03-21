package com.hpe.caf.worker.document;

import com.hpe.caf.worker.testing.TestConfiguration;
import com.hpe.caf.worker.testing.TestItem;
import com.hpe.caf.worker.testing.TestItemProvider;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Result preparation provider for preparing test items.
 * Generates Test items from the yaml serialised test case files.
 */
public class DocumentWorkerResultPreparationProvider  implements TestItemProvider {

    private final TestConfiguration<DocumentWorkerTask,
                                    DocumentWorkerResult,
                                    DocumentWorkerTestInput,
                                    DocumentWorkerTestExpectation> configuration;
    private final String inputPath;
    private final String expectedPath;
    private final String globPattern;
    private final boolean includeSubFolders;

    public DocumentWorkerResultPreparationProvider(final TestConfiguration<DocumentWorkerTask,
                                                                           DocumentWorkerResult,
                                                                           DocumentWorkerTestInput,
                                                                           DocumentWorkerTestExpectation> configuration) {
        this.configuration = configuration;
        this.inputPath = configuration.getTestDocumentsFolder();
        this.expectedPath = configuration.getTestDataFolder();
        this.globPattern = "regex:^(?!.*[.](content|testcase)$).*$";
        this.includeSubFolders = true;
    }

    @Override
    public Collection<TestItem> getItems() throws Exception {

        final List<Path> files = getFiles(Paths.get(inputPath));
        final List<TestItem> testItems = new ArrayList<>(files.size());
        for (final Path inputFile : files) {
            testItems.add(createTestItem(inputFile));
        }
        return testItems;
    }

    private List<Path> getFiles(final Path directory) throws IOException {
        final List<Path> fileNames = new ArrayList<>();
        try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (final Path path : directoryStream) {
                if (Files.isDirectory(path) && includeSubFolders) {
                    fileNames.addAll(getFiles(path));
                }
                else if (globPattern == null || path.getFileSystem().getPathMatcher(globPattern).matches(path.getFileName())) {
                    fileNames.add(path);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
            throw e;
        }
        return fileNames;
    }

    private TestItem createTestItem(final Path inputFile) throws Exception {
        final DocumentWorkerTestInput testInput = configuration.getInputClass().newInstance();
        final String normalizedRelativePath = Paths.get(expectedPath).relativize(inputFile).toString().replace("\\", "/");
        final DocumentWorkerTestExpectation testExpectation = configuration.getExpectationClass().newInstance();
        final TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem =
                new TestItem<>(normalizedRelativePath, testInput, testExpectation);
        testItem.getInputData().setTask(createDocumentWorkerTask(inputFile));
        return testItem;
    }

    private DocumentWorkerTask createDocumentWorkerTask(final Path inputFile) {
        try {
            return configuration.getSerializer().readValue(inputFile.toFile(), configuration.getWorkerTaskClass());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Failed to deserialize inputFile task: " + inputFile + ". Message: " + e.getMessage());
        }
    }
}
