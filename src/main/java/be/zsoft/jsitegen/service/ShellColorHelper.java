package be.zsoft.jsitegen.service;

import be.zsoft.jsitegen.util.PromptColor;
import lombok.RequiredArgsConstructor;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor

@Service
public class ShellColorHelper {

    public String getColored(String message, PromptColor color) {
        return (new AttributedStringBuilder())
                .append(message, AttributedStyle.DEFAULT.foreground(color.toJLineAttributedStyle())).toAnsi();
    }

    public String getInfoMessage(String message) {
        return getColored(message, PromptColor.CYAN);
    }

    public String getSuccessMessage(String message) {
        return getColored(message, PromptColor.GREEN);
    }

    public String getWarningMessage(String message) {
        return getColored(message, PromptColor.YELLOW);
    }

    public String getErrorMessage(String message) {
        return getColored(message, PromptColor.RED);
    }
}
