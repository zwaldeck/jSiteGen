package be.zsoft.jsitegen.service.util;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;


@Service
public class WriterFactory {

    public Writer getWriter(Path path) throws IOException {
        return new FileWriter(path.toFile());
    }
}
