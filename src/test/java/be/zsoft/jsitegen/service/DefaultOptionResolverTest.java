package be.zsoft.jsitegen.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DefaultOptionResolverTest {

    @InjectMocks
    private DefaultOptionResolver resolver;

    @BeforeEach
    public void setup() {
        System.setProperty("user.dir", "/home/ssg");
    }

    @Test
    void getOrDefaultOutputDir_default() {
        assertEquals("/home/ssg%sdist".formatted(File.separator), resolver.getOrDefaultOutputDir(Optional.empty()));
    }

    @Test
    void getOrDefaultInputDir_default() {
        assertEquals("/home/ssg", resolver.getOrDefaultInputDir(Optional.empty()));
    }

    @Test
    void getOrDefaultNewOutputDir_default() {
        assertEquals("/home/ssg%shello".formatted(File.separator), resolver.getOrDefaultNewOutputDir(Optional.empty(), "hello"));
    }
}