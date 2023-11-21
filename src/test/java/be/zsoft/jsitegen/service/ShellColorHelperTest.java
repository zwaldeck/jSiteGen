package be.zsoft.jsitegen.service;

import be.zsoft.jsitegen.util.PromptColor;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ShellColorHelperTest {

    @InjectMocks
    private ShellColorHelper helper;

    @Test
    void getColored() {
        String expected = (new AttributedStringBuilder())
                .append("hello", AttributedStyle.DEFAULT.foreground(PromptColor.BLUE.toJLineAttributedStyle()))
                .toAnsi();

        assertEquals(expected, helper.getColored("hello", PromptColor.BLUE));
    }

    @Test
    void getInfoMessage() {
        String expected = (new AttributedStringBuilder())
                .append("hello", AttributedStyle.DEFAULT.foreground(PromptColor.CYAN.toJLineAttributedStyle()))
                .toAnsi();

        assertEquals(expected, helper.getInfoMessage("hello"));
    }

    @Test
    void getSuccessMessage() {
        String expected = (new AttributedStringBuilder())
                .append("hello", AttributedStyle.DEFAULT.foreground(PromptColor.GREEN.toJLineAttributedStyle()))
                .toAnsi();

        assertEquals(expected, helper.getSuccessMessage("hello"));
    }

    @Test
    void getWarningMessage() {
        String expected = (new AttributedStringBuilder())
                .append("hello", AttributedStyle.DEFAULT.foreground(PromptColor.YELLOW.toJLineAttributedStyle()))
                .toAnsi();

        assertEquals(expected, helper.getWarningMessage("hello"));
    }

    @Test
    void getErrorMessage() {
        String expected = (new AttributedStringBuilder())
                .append("hello", AttributedStyle.DEFAULT.foreground(PromptColor.RED.toJLineAttributedStyle()))
                .toAnsi();

        assertEquals(expected, helper.getErrorMessage("hello"));
    }
}