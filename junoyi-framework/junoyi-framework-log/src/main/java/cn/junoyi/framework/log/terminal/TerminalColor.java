package cn.junoyi.framework.log.terminal;

/**
 * 终端字体颜色
 *
 * @author Fan
 */
public class TerminalColor {
    public static final String RESET = "\033[0m";

    // 前景色
    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";

    // 粗体前景色
    public static final String BOLD_BLACK = "\033[1;30m";
    public static final String BOLD_RED = "\033[1;31m";
    public static final String BOLD_GREEN = "\033[1;32m";
    public static final String BOLD_YELLOW = "\033[1;33m";
    public static final String BOLD_BLUE = "\033[1;34m";
    public static final String BOLD_PURPLE = "\033[1;35m";
    public static final String BOLD_CYAN = "\033[1;36m";
    public static final String BOLD_WHITE = "\033[1;37m";

    // 背景色
    public static final String RED_BACKGROUND = "\033[41m";
    public static final String GREEN_BACKGROUND = "\033[42m";
    public static final String YELLOW_BACKGROUND = "\033[43m";
    public static final String BLUE_BACKGROUND = "\033[44m";
    public static final String PURPLE_BACKGROUND = "\033[45m";
    public static final String CYAN_BACKGROUND = "\033[46m";
    public static final String WHITE_BACKGROUND = "\033[47m";

    // 粗体+背景色组合
    public static final String BOLD_WHITE_ON_RED = "\033[1;37;41m";
    public static final String BOLD_BLACK_ON_YELLOW = "\033[1;30;43m";
    public static final String BOLD_WHITE_ON_GREEN = "\033[1;37;42m";
    public static final String BOLD_WHITE_ON_BLUE = "\033[1;37;44m";
    public static final String BOLD_WHITE_ON_PURPLE = "\033[1;37;45m";
}