package be.zsoft.jsitegen.dto;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public record Page(
        String filename,
        String filenameWithoutExtension,
        String relativePath,
        File file,
        boolean isPost
) {

    public static Page fromFile(File file, String baseDir, boolean isPost) {
        return new Page(
                file.getName(),
                FilenameUtils.removeExtension(file.getName()),
                file.getPath().replace(baseDir, "").replace(file.getName(), ""),
                file,
                isPost
        );
    }
}
