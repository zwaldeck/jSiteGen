package be.zsoft.jsitegen.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PromptColor {
    BLACK(0),
    RED(1),
    GREEN(2),
    YELLOW(3),
    BLUE(4),
    MAGENTA(5),
    CYAN(6),
    WHITE(7),
    BRIGHT(8);

    private final int value;

    public int toJLineAttributedStyle() {
        return value;
    }
}
