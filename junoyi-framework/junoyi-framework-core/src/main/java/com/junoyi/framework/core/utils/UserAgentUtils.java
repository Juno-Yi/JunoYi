package com.junoyi.framework.core.utils;

/**
 * User-Agent 解析工具类
 * 用于从 User-Agent 字符串中解析操作系统和浏览器信息
 *
 * @author Fan
 */
public class UserAgentUtils {

    private UserAgentUtils() {}

    /**
     * 解析操作系统信息
     *
     * @param userAgent User-Agent 字符串
     * @return 操作系统名称，如 "Windows 10", "macOS", "Android 13", "iOS 17"
     */
    public static String parseOS(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "Unknown";
        }

        String ua = userAgent.toLowerCase();

        // Windows
        if (ua.contains("windows nt 10")) {
            return "Windows 10/11";
        } else if (ua.contains("windows nt 6.3")) {
            return "Windows 8.1";
        } else if (ua.contains("windows nt 6.2")) {
            return "Windows 8";
        } else if (ua.contains("windows nt 6.1")) {
            return "Windows 7";
        } else if (ua.contains("windows")) {
            return "Windows";
        }

        // macOS
        if (ua.contains("mac os x")) {
            // 尝试提取版本号
            int idx = ua.indexOf("mac os x");
            if (idx != -1) {
                String sub = ua.substring(idx + 9);
                // 格式可能是 10_15_7 或 10.15.7
                StringBuilder version = new StringBuilder();
                for (char c : sub.toCharArray()) {
                    if (Character.isDigit(c) || c == '_' || c == '.') {
                        version.append(c == '_' ? '.' : c);
                    } else if (version.length() > 0) {
                        break;
                    }
                }
                if (version.length() > 0) {
                    return "macOS " + version;
                }
            }
            return "macOS";
        }

        // iOS (iPhone/iPad)
        if (ua.contains("iphone") || ua.contains("ipad")) {
            String device = ua.contains("ipad") ? "iPadOS" : "iOS";
            // 尝试提取版本
            int idx = ua.indexOf("os ");
            if (idx != -1) {
                String sub = ua.substring(idx + 3);
                StringBuilder version = new StringBuilder();
                for (char c : sub.toCharArray()) {
                    if (Character.isDigit(c) || c == '_') {
                        version.append(c == '_' ? '.' : c);
                    } else if (version.length() > 0) {
                        break;
                    }
                }
                if (version.length() > 0) {
                    return device + " " + version;
                }
            }
            return device;
        }

        // Android
        if (ua.contains("android")) {
            int idx = ua.indexOf("android");
            if (idx != -1) {
                String sub = ua.substring(idx + 8);
                StringBuilder version = new StringBuilder();
                for (char c : sub.toCharArray()) {
                    if (Character.isDigit(c) || c == '.') {
                        version.append(c);
                    } else if (version.length() > 0) {
                        break;
                    }
                }
                if (version.length() > 0) {
                    return "Android " + version;
                }
            }
            return "Android";
        }

        // Linux
        if (ua.contains("linux")) {
            if (ua.contains("ubuntu")) {
                return "Ubuntu";
            } else if (ua.contains("fedora")) {
                return "Fedora";
            } else if (ua.contains("centos")) {
                return "CentOS";
            }
            return "Linux";
        }

        // Chrome OS
        if (ua.contains("cros")) {
            return "Chrome OS";
        }

        return "Unknown";
    }

    /**
     * 解析浏览器信息
     *
     * @param userAgent User-Agent 字符串
     * @return 浏览器名称和版本，如 "Chrome 120", "Firefox 121", "Safari 17"
     */
    public static String parseBrowser(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "Unknown";
        }

        String ua = userAgent;

        // Edge (新版基于 Chromium)
        if (ua.contains("Edg/")) {
            return "Edge " + extractVersion(ua, "Edg/");
        }

        // Opera
        if (ua.contains("OPR/")) {
            return "Opera " + extractVersion(ua, "OPR/");
        }

        // Chrome (必须在 Safari 之前检测，因为 Chrome 的 UA 也包含 Safari)
        if (ua.contains("Chrome/") && !ua.contains("Edg/") && !ua.contains("OPR/")) {
            return "Chrome " + extractVersion(ua, "Chrome/");
        }

        // Firefox
        if (ua.contains("Firefox/")) {
            return "Firefox " + extractVersion(ua, "Firefox/");
        }

        // Safari (必须在 Chrome 之后检测)
        if (ua.contains("Safari/") && !ua.contains("Chrome/")) {
            // Safari 版本在 Version/ 后面
            if (ua.contains("Version/")) {
                return "Safari " + extractVersion(ua, "Version/");
            }
            return "Safari";
        }

        // IE
        if (ua.contains("MSIE")) {
            return "IE " + extractVersion(ua, "MSIE ");
        }
        if (ua.contains("Trident/")) {
            return "IE 11";
        }

        // 微信内置浏览器
        if (ua.contains("MicroMessenger/")) {
            return "微信 " + extractVersion(ua, "MicroMessenger/");
        }

        // 支付宝内置浏览器
        if (ua.contains("AlipayClient/")) {
            return "支付宝";
        }

        // 钉钉
        if (ua.contains("DingTalk/")) {
            return "钉钉";
        }

        return "Unknown";
    }

    /**
     * 解析设备类型
     *
     * @param userAgent User-Agent 字符串
     * @return 设备类型：Mobile/Tablet/Desktop/Unknown
     */
    public static String parseDeviceType(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "Unknown";
        }

        String ua = userAgent.toLowerCase();

        // 平板设备
        if (ua.contains("ipad") || (ua.contains("android") && !ua.contains("mobile"))) {
            return "Tablet";
        }

        // 移动设备
        if (ua.contains("mobile") || ua.contains("iphone") || 
            ua.contains("android") || ua.contains("windows phone")) {
            return "Mobile";
        }

        // 桌面设备
        if (ua.contains("windows") || ua.contains("macintosh") || 
            ua.contains("linux") || ua.contains("cros")) {
            return "Desktop";
        }

        return "Unknown";
    }

    /**
     * 从 User-Agent 中提取版本号
     */
    private static String extractVersion(String ua, String prefix) {
        int idx = ua.indexOf(prefix);
        if (idx == -1) {
            return "";
        }

        String sub = ua.substring(idx + prefix.length());
        StringBuilder version = new StringBuilder();

        for (char c : sub.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                version.append(c);
            } else {
                break;
            }
        }

        // 只保留主版本号
        String ver = version.toString();
        int dotIdx = ver.indexOf('.');
        if (dotIdx > 0) {
            return ver.substring(0, dotIdx);
        }
        return ver;
    }
}
