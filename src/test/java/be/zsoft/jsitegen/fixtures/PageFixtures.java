package be.zsoft.jsitegen.fixtures;

import be.zsoft.jsitegen.dto.Page;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class PageFixtures {

    public static Page anIndexPage() {
        return new Page(
                "index.html",
                "index",
                "",
                new File("index.html"),
                false
        );
    }

    public static Page aBlogPost() {
        return new Page(
                "post1.peb",
                "post1",
                "",
                new File("post1.peb"),
                true
        );
    }
}
