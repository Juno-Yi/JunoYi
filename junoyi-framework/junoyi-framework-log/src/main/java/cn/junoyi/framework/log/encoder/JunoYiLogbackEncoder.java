package cn.junoyi.framework.log.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;
import cn.junoyi.framework.log.terminal.TerminalColor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 框架日志编码器
 * 提供美观的彩色日志输出格式，支持MDC上下文和线程信息
 *
 * @author Fan
 */
public class JunoYiLogbackEncoder extends EncoderBase<ILoggingEvent> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // 是否显示线程名
    private boolean showThreadName = true;
    // 是否显示MDC上下文
    private boolean showMDC = true;
    // 是否显示类名
    private boolean showClassName = true;
    // 最长类名长度（用于格式化对齐）
    private int maxClassNameLength = 20;
    // 是否启用彩色输出
    private boolean colorEnabled = true;

    @Override
    public byte[] encode(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder(256);
        
        // 颜色配置
        String reset = colorEnabled ? TerminalColor.RESET : "";
        String redColor = colorEnabled ? TerminalColor.RED : "";
        String greenColor = colorEnabled ? TerminalColor.GREEN : "";
        String purpleColor = colorEnabled ? TerminalColor.PURPLE : "";
        String cyanColor = colorEnabled ? TerminalColor.CYAN : "";
        
        // 时间戳（红色，更精确的格式）
        sb.append(redColor)
                .append("[")
                .append(formatTimestamp(event.getTimeStamp()))
                .append("] ")
                .append(reset);

        // 线程名（绿色，带括号，固定12字符宽度）
        if (showThreadName) {
            String threadName = formatThread(event.getThreadName());
            sb.append(greenColor)
                    .append("(")
                    .append(String.format("%-10s", threadName))
                    .append(") ")
                    .append(reset);
        }

        // Logger名称（青色，智能缩进，固定30字符宽度）
        String loggerName = event.getLoggerName();
        if (showClassName) {
            String formattedLogger = formatLoggerNameAdvanced(loggerName);
            sb.append(cyanColor)
                    .append(String.format("%-30s", formattedLogger))
                    .append(reset)
                    .append(" ");
        }

        // MDC上下文（紫色，如果有）
        if (showMDC && !event.getMDCPropertyMap().isEmpty()) {
            sb.append(purpleColor)
                    .append("[MDC: ")
                    .append(formatMDC(event.getMDCPropertyMap()))
                    .append("] ")
                    .append(reset);
        }

        // 日志级别（彩色背景，固定7字符宽度，文字居中）
        String levelText = event.getLevel().toString();
        String levelStr;
        switch (levelText.length()) {
            case 4: // INFO
                levelStr = "[" + levelText + "] ";
                break;
            case 5: // ERROR, DEBUG, TRACE
                levelStr = "[" + levelText + "]";
                break;
            case 3: // WARN
                levelStr = "[WARN ] ";
                break;
            default:
                levelStr = "[" + levelText + "]";
                break;
        }
        
        String levelColor = getLevelColor(event.getLevel());
        sb.append(colorEnabled ? levelColor : "")
                .append(String.format("%7s", levelStr))
                .append(reset)
                .append(" ");

        // 日志消息（根据级别着色）
        String messageColor = getMessageColor(event.getLevel());
        sb.append(colorEnabled ? messageColor : "")
                .append(event.getFormattedMessage())
                .append(reset);

        // 异常信息（红色，带缩进）
        if (event.getThrowableProxy() != null) {
            sb.append("\n").append(formatThrowableAdvanced(event.getThrowableProxy()));
        }

        sb.append("\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 根据日志级别获取颜色
     */
    private String getLevelColor(ch.qos.logback.classic.Level level) {
        if (!colorEnabled) {
            return "";
        }
        
        switch (level.toInt()) {
            case ch.qos.logback.classic.Level.ERROR_INT:
                return TerminalColor.BOLD_WHITE_ON_RED;
            case ch.qos.logback.classic.Level.WARN_INT:
                return TerminalColor.BOLD_BLACK_ON_YELLOW;
            case ch.qos.logback.classic.Level.INFO_INT:
                return TerminalColor.BOLD_WHITE_ON_GREEN;
            case ch.qos.logback.classic.Level.DEBUG_INT:
                return TerminalColor.BOLD_WHITE_ON_BLUE;
            case ch.qos.logback.classic.Level.TRACE_INT:
                return TerminalColor.BOLD_WHITE_ON_PURPLE;
            default:
                return TerminalColor.GREEN;
        }
    }

    /**
     * 根据日志级别获取消息颜色
     */
    private String getMessageColor(ch.qos.logback.classic.Level level) {
        if (!colorEnabled) {
            return "";
        }
        
        switch (level.toInt()) {
            case ch.qos.logback.classic.Level.ERROR_INT:
                return TerminalColor.BOLD_RED;
            case ch.qos.logback.classic.Level.WARN_INT:
                return TerminalColor.BOLD_YELLOW;
            case ch.qos.logback.classic.Level.INFO_INT:
                return ""; // INFO级别使用默认样式，不重置颜色
            case ch.qos.logback.classic.Level.DEBUG_INT:
                return TerminalColor.BOLD_BLUE;
            case ch.qos.logback.classic.Level.TRACE_INT:
                return TerminalColor.BOLD_PURPLE;
            default:
                return TerminalColor.WHITE;
        }
    }

    /**
     * 高级Logger名称格式化
     */
    private String formatLoggerNameAdvanced(String loggerName) {
        if (loggerName == null) {
            return "";
        }
        
        // 移除前缀
        String simplified = loggerName.replace("cn.junoyi.", "");
        
        // 智能缩写
        if (simplified.length() > 28) {
            String[] parts = simplified.split("\\.");
            if (parts.length > 2) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < parts.length - 1; i++) {
                    if (parts[i].length() > 0) {
                        sb.append(parts[i].charAt(0)).append(".");
                    }
                }
                sb.append(parts[parts.length - 1]);
                simplified = sb.toString();
            }
        }
        
        // 最终截断
        if (simplified.length() > 28) {
            simplified = simplified.substring(0, 25) + "...";
        }
        
        return simplified;
    }

    /**
     * 高级异常信息格式化
     */
    private String formatThrowableAdvanced(ch.qos.logback.classic.spi.IThrowableProxy throwableProxy) {
        if (throwableProxy == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        String redColor = colorEnabled ? TerminalColor.RED : "";
        String yellowColor = colorEnabled ? TerminalColor.YELLOW : "";
        String reset = colorEnabled ? TerminalColor.RESET : "";
        
        sb.append(redColor)
                .append("┌─ 异常堆栈跟踪 ")
                .append("─".repeat(60))
                .append("\n")
                .append("│ ")
                .append(yellowColor)
                .append(throwableProxy.getClassName())
                .append(": ")
                .append(throwableProxy.getMessage())
                .append(reset)
                .append("\n");
        
        ch.qos.logback.classic.spi.StackTraceElementProxy[] steArray = throwableProxy.getStackTraceElementProxyArray();
        for (int i = 0; i < Math.min(steArray.length, 8); i++) {
            sb.append(redColor).append("│ ").append(reset)
                    .append("   at ")
                    .append(colorEnabled ? TerminalColor.CYAN : "")
                    .append(steArray[i].getSTEAsString())
                    .append(reset)
                    .append("\n");
        }
        
        if (steArray.length > 8) {
            sb.append(redColor)
                    .append("│ ")
                    .append(yellowColor)
                    .append("   ... ")
                    .append(steArray.length - 8)
                    .append(" more")
                    .append(reset)
                    .append("\n");
        }
        
        sb.append(redColor)
                .append("└─")
                .append("─".repeat(75))
                .append(reset);
        
        return sb.toString();
    }

    /**
     * 格式化时间戳
     */
    private String formatTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(timestamp), 
            java.time.ZoneId.systemDefault()
        ).format(DATE_FORMATTER);
    }

    /**
     * 格式化线程名
     */
    private String formatThread(String threadName) {
        if (threadName == null) {
            return "unknown";
        }
        // 简化常见线程名
        switch (threadName) {
            case "main": return "main";
            case "restartedMain": return "main";
            default:
                // 如果太长则截取后12个字符
                return threadName.length() > 12 ? "..." + threadName.substring(threadName.length() - 9) : threadName;
        }
    }

    /**
     * 格式化MDC上下文
     */
    private String formatMDC(java.util.Map<String, String> mdcMap) {
        if (mdcMap == null || mdcMap.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        mdcMap.entrySet().stream()
                .limit(3) // 最多显示3个MDC属性
                .forEach(entry -> sb.append(entry.getKey()).append("=").append(entry.getValue()).append(","));
        
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1); // 移除最后的逗号
        }
        sb.append("]");
        return sb.toString();
    }

    // === Getter/Setter方法 ===
    
    public boolean isShowThreadName() {
        return showThreadName;
    }

    public void setShowThreadName(boolean showThreadName) {
        this.showThreadName = showThreadName;
    }

    public boolean isShowMDC() {
        return showMDC;
    }

    public void setShowMDC(boolean showMDC) {
        this.showMDC = showMDC;
    }

    public boolean isShowClassName() {
        return showClassName;
    }

    public void setShowClassName(boolean showClassName) {
        this.showClassName = showClassName;
    }

    public int getMaxClassNameLength() {
        return maxClassNameLength;
    }

    public void setMaxClassNameLength(int maxClassNameLength) {
        this.maxClassNameLength = Math.max(10, maxClassNameLength);
    }

    public boolean isColorEnabled() {
        return colorEnabled;
    }

    public void setColorEnabled(boolean colorEnabled) {
        this.colorEnabled = colorEnabled;
    }

    @Override
    public byte[] headerBytes() { 
        return null; 
    }
    
    @Override
    public byte[] footerBytes() { 
        return null; 
    }
}