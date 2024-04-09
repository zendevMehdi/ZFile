package org.zendev.lib.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;


public class FolderVisitor extends SimpleFileVisitor<Path> {
    private List<Path> visited;
    private List<Path> visitedFailed;

    private boolean showHidden;

    public FolderVisitor(boolean showHidden) {
        visited = new ArrayList<>();
        visitedFailed = new ArrayList<>();

        this.showHidden = showHidden;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        try {
            if (showHidden && !Files.isHidden(file)) {
                visited.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            visitedFailed.add(file);
        } finally {
            if (!visited.contains(file)) {
                visited.add(file);
            }
        }

        return super.visitFile(file, attrs);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        visitedFailed.add(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (showHidden && !Files.isHidden(dir)) {
            visited.add(dir);
        }

        return super.postVisitDirectory(dir, exc);
    }

    public List<Path> getVisited() {
        return visited;
    }

    public List<Path> getVisitedFailed() {
        return visitedFailed;
    }

    public boolean isShowHidden() {
        return showHidden;
    }

    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }
}
