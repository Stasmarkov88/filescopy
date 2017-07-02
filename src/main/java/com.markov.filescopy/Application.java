package com.markov.filescopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Markov on 18.02.2017.
 */
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final String PATTERN = "yyyy-MM-dd";

    private static boolean isTestMode = false;

    public static void main(String[] args) throws IOException {
        setApplicationMode(args);

        String source = args[0];
        String destination = args[1];

        Set<FileParameters> sourceFiles = new HashSet<>();
        Set<FileParameters> destinationFiles = new HashSet<>();

        Files.walkFileTree(Paths.get(source), addFilesVisitor(sourceFiles));
        Files.walkFileTree(Paths.get(destination), addFilesVisitor(destinationFiles));

        LOGGER.info("Found files in source path:" + sourceFiles.size());
        LOGGER.info("Found files in destination path:" + destinationFiles.size());

        if (!sourceFiles.isEmpty()) {
            Set<FileParameters> newFiles = sourceFiles.stream()
                    .filter(sourceFile -> !destinationFiles.contains(sourceFile))
                    .collect(Collectors.toSet());
            if (!newFiles.isEmpty()) {
                LOGGER.info(newFiles.size() + " new files found");
                newFiles.forEach(file -> LOGGER.info(file.toString()));
                if (!isTestMode) {
                    copyFiles(destination, newFiles);
                }
            }
        } else {
            LOGGER.info("new files were not found");
        }

        LOGGER.info("Exit");
        System.exit(0);
    }

    private static void copyFiles(String destination, Set<FileParameters> newFiles) throws IOException {
        String newDirectoryName = new SimpleDateFormat(PATTERN).format(new Date());
        Path newDirectoryPath = Paths.get(destination, newDirectoryName).toAbsolutePath();
        if (!newDirectoryPath.toFile().exists()) {
            Files.createDirectory(newDirectoryPath);
        }
        for (FileParameters newFile : newFiles) {
            LOGGER.info("Copying file: " + newFile.getFileName());
            try {
                Files.copy(Paths.get(newFile.getAbsolutePath()), newDirectoryPath.resolve(newFile.getFileName()));
            } catch (IOException e) {
                LOGGER.error("failed to copy file {}", newFile);
                throw e;
            }
        }
    }

    private static void setApplicationMode(String[] args) {
        switch (args.length) {
            case 2: {
                LOGGER.info("start application in real mode");
                break;
            }
            case 3: {
                assert args[2].equals("test") : "for test mode put 'test' as third parameter";
                isTestMode = true;
                LOGGER.info("start application in test mode");
                break;
            }
            default:
                throw new IllegalArgumentException("Number of parameters is incorrect: " + args.length);
        }
    }

    private static FileVisitor<? super Path> addFilesVisitor(final Set<FileParameters> sourceFiles) {
        return new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                sourceFiles.add(new FileParameters(file.toFile().getName(), file.toFile().getAbsolutePath(), file.toFile().length()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                sourceFiles.add(new FileParameters(file.toFile().getName(), file.toFile().getAbsolutePath(), file.toFile().length()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        };
    }
}
