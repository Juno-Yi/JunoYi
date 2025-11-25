package cn.junoyi.framework.log.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JunoYi框架日志配置属性
 * 支持通过application.yml或application.properties进行配置
 *
 * @author Fan
 */
@ConfigurationProperties(prefix = "junoyi.log")
public class JunoYiLogProperties {

    /**
     * 是否启用JunoYi日志框架
     */
    private boolean enabled = true;

    /**
     * 控制台输出配置
     */
    private Console console = new Console();

    /**
     * 文件输出配置
     */
    private File file = new File();

    /**
     * 日志级别配置
     */
    private Level level = new Level();

    /**
     * 日志格式配置
     */
    private Format format = new Format();

    /**
     * 异步日志配置
     */
    private Async async = new Async();

    /**
     * 控制台输出配置
     */
    public static class Console {
        /**
         * 是否启用控制台输出
         */
        private boolean enabled = true;

        /**
         * 是否启用彩色输出
         */
        private boolean colorEnabled = true;

        /**
         * 是否显示线程名
         */
        private boolean showThreadName = true;

        /**
         * 是否显示MDC上下文
         */
        private boolean showMDC = true;

        /**
         * 是否显示类名
         */
        private boolean showClassName = true;

        /**
         * 类名最大长度
         */
        private int maxClassNameLength = 20;

        // getters and setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isColorEnabled() {
            return colorEnabled;
        }

        public void setColorEnabled(boolean colorEnabled) {
            this.colorEnabled = colorEnabled;
        }

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
    }

    /**
     * 文件输出配置
     */
    public static class File {
        /**
         * 是否启用文件输出
         */
        private boolean enabled = false;

        /**
         * 日志文件路径
         */
        private String path = "logs/junoyi.log";

        /**
         * 日志文件最大大小（支持KB,MB,GB）
         */
        private String maxSize = "100MB";

        /**
         * 保留的历史日志文件数量
         */
        private int maxHistory = 30;

        /**
         * 日志文件总大小限制
         */
        private String totalSizeCap = "1GB";

        /**
         * 是否启用压缩
         */
        private boolean compress = true;

        /**
         * 日志文件编码
         */
        private String encoding = "UTF-8";

        // getters and setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(String maxSize) {
            this.maxSize = maxSize;
        }

        public int getMaxHistory() {
            return maxHistory;
        }

        public void setMaxHistory(int maxHistory) {
            this.maxHistory = Math.max(1, maxHistory);
        }

        public String getTotalSizeCap() {
            return totalSizeCap;
        }

        public void setTotalSizeCap(String totalSizeCap) {
            this.totalSizeCap = totalSizeCap;
        }

        public boolean isCompress() {
            return compress;
        }

        public void setCompress(boolean compress) {
            this.compress = compress;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }
    }

    /**
     * 日志级别配置
     */
    public static class Level {
        /**
         * 根日志级别
         */
        private String root = "WARN";

        /**
         * JunoYi框架日志级别
         */
        private String junoyi = "INFO";

        /**
         * Spring框架日志级别
         */
        private String spring = "WARN";

        /**
         * MyBatis日志级别
         */
        private String mybatis = "WARN";

        /**
         * 数据库SQL日志级别
         */
        private String sql = "DEBUG";

        /**
         * 自定义包日志级别配置
         * 格式：package1:level1,package2:level2
         */
        private String custom;

        // getters and setters
        public String getRoot() {
            return root;
        }

        public void setRoot(String root) {
            this.root = root;
        }

        public String getJunoyi() {
            return junoyi;
        }

        public void setJunoyi(String junoyi) {
            this.junoyi = junoyi;
        }

        public String getSpring() {
            return spring;
        }

        public void setSpring(String spring) {
            this.spring = spring;
        }

        public String getMybatis() {
            return mybatis;
        }

        public void setMybatis(String mybatis) {
            this.mybatis = mybatis;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public String getCustom() {
            return custom;
        }

        public void setCustom(String custom) {
            this.custom = custom;
        }
    }

    /**
     * 日志格式配置
     */
    public static class Format {
        /**
         * 日期时间格式
         */
        private String dateTimePattern = "yyyy-MM-dd HH:mm:ss.SSS";

        /**
         * 异常堆栈最大行数
         */
        private int maxStackTraceLines = 5;

        /**
         * MDC最大显示数量
         */
        private int maxMDCProperties = 3;

        /**
         * 线程名最大长度
         */
        private int maxThreadNameLength = 15;

        /**
         * 是否简化包名显示
         */
        private boolean simplifyPackageNames = true;

        /**
         * 包名简化规则
         */
        private java.util.Map<String, String> packageSimplifications = new java.util.HashMap<>();

        public Format() {
            // 默认包名简化规则
            packageSimplifications.put("cn.junoyi", "j");
            packageSimplifications.put("org.springframework", "s");
            packageSimplifications.put("com.baomidou", "m");
            packageSimplifications.put("org.apache", "a");
            packageSimplifications.put("java.lang", "j.l");
            packageSimplifications.put("java.util", "j.u");
        }

        // getters and setters
        public String getDateTimePattern() {
            return dateTimePattern;
        }

        public void setDateTimePattern(String dateTimePattern) {
            this.dateTimePattern = dateTimePattern;
        }

        public int getMaxStackTraceLines() {
            return maxStackTraceLines;
        }

        public void setMaxStackTraceLines(int maxStackTraceLines) {
            this.maxStackTraceLines = Math.max(1, maxStackTraceLines);
        }

        public int getMaxMDCProperties() {
            return maxMDCProperties;
        }

        public void setMaxMDCProperties(int maxMDCProperties) {
            this.maxMDCProperties = Math.max(1, maxMDCProperties);
        }

        public int getMaxThreadNameLength() {
            return maxThreadNameLength;
        }

        public void setMaxThreadNameLength(int maxThreadNameLength) {
            this.maxThreadNameLength = Math.max(5, maxThreadNameLength);
        }

        public boolean isSimplifyPackageNames() {
            return simplifyPackageNames;
        }

        public void setSimplifyPackageNames(boolean simplifyPackageNames) {
            this.simplifyPackageNames = simplifyPackageNames;
        }

        public java.util.Map<String, String> getPackageSimplifications() {
            return packageSimplifications;
        }

        public void setPackageSimplifications(java.util.Map<String, String> packageSimplifications) {
            this.packageSimplifications = packageSimplifications;
        }
    }

    /**
     * 异步日志配置
     */
    public static class Async {
        /**
         * 是否启用异步日志
         */
        private boolean enabled = false;

        /**
         * 异步队列大小
         */
        private int queueSize = 256;

        /**
         * 丢弃策略：DISCARD, BLOCK, ASYNC
         */
        private String discardingThreshold = "DISCARD";

        /**
         * 是否包含调用者数据
         */
        private boolean includeCallerData = false;

        /**
         * 异步线程池大小
         */
        private int threadPoolSize = 1;

        // getters and setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = Math.max(1, queueSize);
        }

        public String getDiscardingThreshold() {
            return discardingThreshold;
        }

        public void setDiscardingThreshold(String discardingThreshold) {
            this.discardingThreshold = discardingThreshold;
        }

        public boolean isIncludeCallerData() {
            return includeCallerData;
        }

        public void setIncludeCallerData(boolean includeCallerData) {
            this.includeCallerData = includeCallerData;
        }

        public int getThreadPoolSize() {
            return threadPoolSize;
        }

        public void setThreadPoolSize(int threadPoolSize) {
            this.threadPoolSize = Math.max(1, threadPoolSize);
        }
    }

    // === 主要配置类的getter/setter ===

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Console getConsole() {
        return console;
    }

    public void setConsole(Console console) {
        this.console = console;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Async getAsync() {
        return async;
    }

    public void setAsync(Async async) {
        this.async = async;
    }
}
