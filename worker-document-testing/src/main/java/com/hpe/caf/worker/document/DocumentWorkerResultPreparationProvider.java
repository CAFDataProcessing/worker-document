package com.hpe.caf.worker.document;

import com.hpe.caf.worker.document.DocumentWorkerResult;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.testing.TestConfiguration;
import com.hpe.caf.worker.testing.TestItem;
import com.hpe.caf.worker.testing.TestItemProvider;
import com.hpe.caf.worker.testing.preparation.PreparationItemProvider;

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

    private final TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> configuration;
    private final String inputPath;
    private final String expectedPath;
    private final String globPattern;
    private final boolean includeSubFolders;

    public DocumentWorkerResultPreparationProvider(final TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> configuration) {
        this.configuration = configuration;
        this.inputPath = configuration.getTestDocumentsFolder();
        this.expectedPath = configuration.getTestDataFolder();
        this.globPattern = "regex:^(?!.*[.](content|testcase)$).*$";
        this.includeSubFolders = true;
    }

    @Override
    public Collection<TestItem> getItems() throws Exception {

        List<Path> files = getFiles(Paths.get(inputPath));

        List<TestItem> testItems = new ArrayList<>(files.size());
        for (Path inputFile : files) {
            testItems.add(createTestItem(inputFile));
        }
        return testItems;
    }

    private List<Path> getFiles(Path directory) throws IOException {
        List<Path> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {

                if (Files.isDirectory(path)){
                    if (includeSubFolders) {
                        fileNames.addAll(getFiles(path));
                    }
                }
                else {

                    if (globPattern == null || path.getFileSystem().getPathMatcher(globPattern).matches(path.getFileName())) {
                        fileNames.add(path);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
            throw ex;
        }
        return fileNames;
    }

    private TestItem createTestItem(Path inputFile) throws Exception {

        DocumentWorkerTestInput testInput = configuration.getInputClass().newInstance();

        Path basePath = Paths.get(expectedPath);

        Path relativePath = basePath.relativize(inputFile);
        String normalizedRelativePath = relativePath.toString().replace("\\", "/");

        DocumentWorkerTestExpectation testExpectation = configuration.getExpectationClass().newInstance();

        TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem = new TestItem<>(normalizedRelativePath, testInput, testExpectation);

        testItem.getInputData().setTask(createDocumentWorkerTask(inputFile));

        return testItem;
    }

    private DocumentWorkerTask createDocumentWorkerTask(Path inputFile) {

        try {
            DocumentWorkerTask task = configuration.getSerializer().readValue(inputFile.toFile(), configuration.getWorkerTaskClass());
            return task;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Failed to deserialize inputFile task: " + inputFile + ". Message: " + e.getMessage());
        }
    }
}
