package com.github.paulakimenko.fakeses.dao;

import com.github.paulakimenko.fakeses.BaseSESMockTest;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public abstract class BaseFileDAOTest extends BaseSESMockTest {
    private static final String TEST_DIR = "tmp";
    private static final Path TEST_DIR_PATH = Paths.get(TEST_DIR);

    protected static MessagesDAO testDao;

    @BeforeClass
    public static void setUpClass() throws Exception {
        testDao = MessagesFileDAO.prepareFor(TEST_DIR);

        URL testMessagesUrl = Optional
                .ofNullable(Thread
                        .currentThread()
                        .getContextClassLoader()
                        .getResource("test_messages"))
                .orElseThrow(() -> new NullPointerException("Test messages URL is null"));

        Files.walkFileTree(Paths.get(testMessagesUrl.toURI()), new CopyFileVisitor(TEST_DIR_PATH));
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        Files.walkFileTree(TEST_DIR_PATH, new DeleteFileVisitor());
    }

    protected static void assertTestDirIsEmpty() {
        assertTrue("Test dir exists", isDirEmpty(TEST_DIR_PATH));
    }

    protected static void assertFileExists(Path filePath, boolean isDir) {
        Path path = TEST_DIR_PATH.resolve(filePath);

        assertTrue(format("File %s not exists", path), Files.exists(path));
        if (isDir) {
            assertTrue(format("%s isn't dir", path), Files.isDirectory(path));
        }
    }

    protected static void assertFileContent(Path filePath, Matcher<String> contentMatcher) {
        Path path = TEST_DIR_PATH.resolve(filePath);

        try {
            String fileContent = new String(Files.readAllBytes(path));
            assertThat(fileContent, contentMatcher);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isDirEmpty(final Path directory) {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class DeleteFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }

    private static class CopyFileVisitor extends SimpleFileVisitor<Path> {
        private final Path targetPath;
        private Path sourcePath = null;

        public CopyFileVisitor(Path targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            sourcePath = Optional.ofNullable(sourcePath).orElse(dir);
            Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
            return FileVisitResult.CONTINUE;
        }
    }
}
