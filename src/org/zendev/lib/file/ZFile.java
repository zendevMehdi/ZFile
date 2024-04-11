package org.zendev.lib.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.zendev.lib.file.options.FileTimeAttribute;
import org.zendev.lib.file.options.FolderListType;
import org.zendev.lib.file.options.ItemType;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;

public class ZFile {
    private String path;

    public ZFile(String path) {
        setPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = new File(path).getAbsolutePath();
    }

    private boolean isFolder() {
        return Files.isDirectory(Path.of(path));
    }

    private void checkItem() throws IOException {
        if (!Files.exists(Path.of(path))) {
            throw new IOException("No such file or folder exists.");
        }
    }

    public boolean isEmpty() throws IOException {
        if (isFolder()) {
            return FileUtils.isEmptyDirectory(new File(path));
        }

        return Files.size(Path.of(path)) == 0;
    }

    public boolean isLocked() {
        boolean blocked = false;
        File currentFile = new File(path);

        try (RandomAccessFile fis = new RandomAccessFile(currentFile, "rw")) {
            FileLock lck = fis.getChannel().lock();
            lck.release();
            lck.close();
        } catch (Exception ex) {
            if (!isFolder()) {
                blocked = true;
            }
        }

        if (!blocked) {
            String parent = currentFile.getParent(), rnd = UUID.randomUUID().toString();

            File newName = new File(parent + "/" + rnd);
            if (currentFile.renameTo(newName)) {
                newName.renameTo(currentFile);
            } else {
                blocked = true;
            }
        }

        return blocked;
    }

    public boolean exists(ItemType type) {
        Path pth = Path.of(path);
        if (!Files.exists(pth)) {
            return false;
        }

        return switch (type) {
            case FILE -> Files.isRegularFile(pth);
            case FOLDER -> Files.isDirectory(pth);
            case ANY -> true;
        };
    }

    public String getName() throws IOException {
        checkItem();
        if (Arrays.asList(File.listRoots()).contains(new File(path))) {
            return path;
        }

        return Path.of(path).getFileName().toString();
    }

    public String getBaseName() throws IOException {
        checkItem();
        if (Arrays.asList(File.listRoots()).contains(new File(path))) {
            return path;
        }

        return FilenameUtils.getBaseName(path);
    }

    public String getExtension() throws IOException {
        checkItem();
        if (Arrays.asList(File.listRoots()).contains(new File(path))) {
            return path;
        }

        return FilenameUtils.getExtension(path);
    }

    public String getOwner(boolean fullOwnerName) throws IOException {
        String ownerName = Files.getOwner(Path.of(path)).getName();
        if (fullOwnerName) {
            return ownerName;
        }

        return ownerName.split("\\\\")[1];
    }

    public long getSize() {
        if (isFolder()) {
            return FileUtils.sizeOfDirectory(new File(path));
        }

        return FileUtils.sizeOf(new File(path));
    }

    public String getType() throws IOException {
        if (isFolder()) {
            return "Folder";
        }

        return new Tika().detect(new File(path));
    }

    public String getParentPath() {
        if (Arrays.asList(File.listRoots()).contains(new File(path))) {
            return path;
        }

        Path pth = Path.of(path);

        if (pth.getParent() == null) {
            return pth.getRoot().toString();
        }

        return new File(path).getParentFile().getAbsolutePath();
    }

    public String getParentName() {
        if (Arrays.asList(File.listRoots()).contains(new File(path))) {
            return path;
        }

        Path pth = Path.of(path);

        if (pth.getParent() == null) {
            return pth.getRoot().toString();
        }

        if (pth.getParent().getFileName() == null) {
            return path.substring(0, 3);
        }

        return pth.getParent().getFileName().toString();
    }

    public String getTimeAttribute(FileTimeAttribute attribute) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(Path.of(path), BasicFileAttributes.class);
        FileTime time;

        switch (attribute) {
            case MODIFY_DATE -> {
                time = attrs.lastModifiedTime();
                return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time.toMillis()));
            }
            case CREATE_DATE -> {
                time = attrs.creationTime();
                return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time.toMillis()));
            }

            case ACCESS_DATE -> {
                time = attrs.lastAccessTime();
                return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time.toMillis()));
            }

            case MODIFY_TIME -> {
                time = attrs.lastModifiedTime();
                return new SimpleDateFormat("HH:mm:ss").format(new Date(time.toMillis()));
            }

            case CREATE_TIME -> {
                time = attrs.creationTime();
                return new SimpleDateFormat("HH:mm:ss").format(new Date(time.toMillis()));
            }

            case ACCESS_TIME -> {
                time = attrs.lastAccessTime();
                return new SimpleDateFormat("HH:mm:ss").format(new Date(time.toMillis()));
            }

            default -> {
                return "";
            }
        }
    }

    public Icon getIcon() {
        return FileSystemView.getFileSystemView().getSystemIcon(new File(path));
    }

    public boolean delete() throws IOException {
        if (isFolder()) {
            FileUtils.deleteDirectory(new File(path));
        } else {
            FileUtils.delete(new File(path));
        }

        return true;
    }

    public boolean rename(String newName) throws IOException {
        String parentFolder = new File(path).getParentFile().getAbsolutePath();
        return new File(path).renameTo(new File(FilenameUtils.normalize(parentFolder + "\\" + newName)));
    }

    public boolean copy(String destination) throws IOException {
        if (isFolder()) {
            FileUtils.copyDirectory(new File(path), new File(destination));
        } else {
            FileUtils.copyFile(new File(path), new File(destination));
        }

        return true;
    }

    public List<Path> list(FolderListType type, boolean showHidden) {
        if (isFolder()) {
            List<File> tmp = List.of(
                    FileSystemView.getFileSystemView().getFiles(new File(path), !showHidden)
            );

            List<Path> content = new ArrayList<>();
            switch (type) {
                case FILE -> {
                    tmp = tmp.stream().filter(x -> !x.isDirectory()).toList();
                }

                case FOLDER -> {
                    tmp = tmp.stream().filter(File::isDirectory).toList();
                }
            }

            tmp.forEach(file -> content.add(file.toPath()));
            return content;
        }

        return List.of(Path.of(path));
    }

    public FolderVisitor walk(boolean showHidden) throws IOException {
        if (isFolder()) {
            FolderVisitor visitor = new FolderVisitor(showHidden);
            Files.walkFileTree(Path.of(path), visitor);

            visitor.getVisited().remove(visitor.getVisited().size() - 1);
            return visitor;
        }

        return null;
    }

    public boolean clear() throws IOException {
        if (isFolder()) {
            FileUtils.cleanDirectory(new File(path));
        } else {
            Files.write(Path.of(path), new byte[]{});
        }

        return true;
    }
}
