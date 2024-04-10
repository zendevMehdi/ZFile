package org.zendev.lib.file.search;

import org.zendev.lib.file.FolderVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FolderSearchEngine {
    private final String path;
    private String key;

    private final boolean exactMatch;
    private final boolean caseSensitive;

    private FolderSearchEngine(FolderSearchEngineBuilder builder) {
        this.path = builder.path;
        this.key = builder.key;

        this.exactMatch = builder.exactMatch;
        this.caseSensitive = builder.caseSensitive;
    }

    public boolean search(boolean showHidden) throws IOException {
        FolderVisitor visitor = new FolderVisitor(showHidden);
        Files.walkFileTree(Path.of(path), visitor);

        visitor.getVisited().remove(visitor.getVisited().size() - 1);
        for (Path pth : visitor.getVisited()) {
            String name = pth.getFileName().toString();

            if (!caseSensitive) {
                name = name.toLowerCase();
                key = key.toLowerCase();
            }

            if (exactMatch) {
                if (name.equals(key)) {
                    return true;
                }
            } else {
                if (name.contains(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static class FolderSearchEngineBuilder {
        private String path;
        private String key;

        private boolean exactMatch;
        private boolean caseSensitive;

        public FolderSearchEngineBuilder(String path, String key) {
            this.path = path;
            this.key = key;

            this.exactMatch = false;
            this.caseSensitive = true;
        }

        public FolderSearchEngineBuilder setPath(String path) {
            this.path = path;
            return this;
        }

        public FolderSearchEngineBuilder setKey(String key) {
            this.key = key;
            return this;
        }

        public FolderSearchEngineBuilder setExactMatch(boolean exactMatch) {
            this.exactMatch = exactMatch;
            return this;
        }

        public FolderSearchEngineBuilder setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public FolderSearchEngine build() {
            return new FolderSearchEngine(this);
        }
    }
}
