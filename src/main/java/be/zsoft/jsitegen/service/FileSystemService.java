package be.zsoft.jsitegen.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

@RequiredArgsConstructor

@Service
public class FileSystemService {

    public void copyFileFromResourcesToPath(String absoluteResourcePath, Path copyToPath) throws IOException {
        try (InputStream is = FileSystemService.class.getResourceAsStream(absoluteResourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: %s".formatted(absoluteResourcePath));
            }

            Files.copy(is, copyToPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void copyDirectoryFromResourcesToPath(String absoluteResourcePath, Path copyToPath) throws Exception {
        URL resourceURL = FileSystemService.class.getResource(absoluteResourcePath);
        if (resourceURL == null) {
            throw new IOException("Resource not found: %s".formatted(absoluteResourcePath));
        }

        Path resourcePath = Path.of(resourceURL.toURI());

        FileUtils.copyDirectory(resourcePath.toFile(), copyToPath.toFile());
    }

    public void createDirectories(Path dir) throws IOException {
        Files.createDirectories(dir);
    }

    public void writeFile(Path outputPath, String content) throws IOException {
        Files.write(outputPath, content.getBytes());
    }

    public boolean exists(Path path) {
        return Files.exists(path);
    }

    public void copyDirectory(Path from, Path to) throws IOException {
        FileUtils.copyDirectory(from.toFile(), to.toFile());
    }

    public void deleteDirectory(Path path) throws IOException{
        FileUtils.deleteDirectory(path.toFile());
    }

    public Collection<File> listFilesWithExtension(Path path, String extension) {
        IOFileFilter htmlFileFilter = FileFilterUtils.suffixFileFilter("." + extension);
        IOFileFilter filter = FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), htmlFileFilter);

        return FileUtils.listFiles(path.toFile(), filter, FileFilterUtils.trueFileFilter());
    }
}
