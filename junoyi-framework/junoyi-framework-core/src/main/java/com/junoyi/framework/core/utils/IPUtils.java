package com.junoyi.framework.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP 工具类
 *
 * @author Fan
 */
public class IPUtils {

    private static final Logger log = LoggerFactory.getLogger(IPUtils.class);

    /**
     * ip2region 搜索器（内存模式）
     */
    private static Searcher searcher;

    /**
     * 初始化标志
     */
    private static volatile boolean initialized = false;

    /**
     * 初始化 ip2region 搜索器
     */
    private static void initSearcher() {
        if (initialized) {
            return;
        }
        synchronized (IPUtils.class) {
            if (initialized) {
                return;
            }
            try {
                ClassPathResource resource = new ClassPathResource("ip2region/ip2region.xdb");
                try (InputStream inputStream = resource.getInputStream()) {
                    byte[] cBuff = inputStream.readAllBytes();
                    searcher = Searcher.newWithBuffer(cBuff);
                    initialized = true;
                    log.info("ip2region 数据库加载成功");
                }
            } catch (Exception e) {
                log.warn("ip2region 数据库加载失败，IP地区查询功能将不可用: {}", e.getMessage());
            }
        }
    }

    /**
     * 根据IP地址获取地区信息
     *
     * @param ip IP地址
     * @return 地区信息，格式如: "中国|0|广东省|深圳市|电信"，失败返回null
     */
    public static String getIpRegion(String ip) {
        if (StringUtils.isBlank(ip) || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "内网IP";
        }

        // 内网IP直接返回
        if (internalIp(ip)) {
            return "内网IP";
        }

        // 延迟初始化
        if (!initialized) {
            initSearcher();
        }

        if (searcher == null) {
            return null;
        }

        try {
            String region = searcher.search(ip);
            return formatRegion(region);
        } catch (Exception e) {
            log.debug("IP地区查询失败: ip={}, error={}", ip, e.getMessage());
            return null;
        }
    }

    /**
     * 格式化地区信息
     * 原始格式: "中国|0|广东省|深圳市|电信"
     * 格式化后: "中国 广东省 深圳市 电信" (去除0和重复项)
     */
    private static String formatRegion(String region) {
        if (StringUtils.isBlank(region)) {
            return null;
        }

        String[] parts = region.split("\\|");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            if (StringUtils.isNotBlank(part) && !"0".equals(part)) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(part);
            }
        }

        return sb.length() > 0 ? sb.toString() : null;
    }

    public static final String REGX_0_255 = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
    public static final String REGX_IP = "((" + REGX_0_255 + "\\.){3}" + REGX_0_255 + ")";
    public static final String REGX_IP_WILDCARD = "(((\\*\\.){3}\\*)|(" + REGX_0_255 + "(\\.\\*){3})|(" + REGX_0_255 + "\\." + REGX_0_255 + ")(\\.\\*){2}" + "|((" + REGX_0_255 + "\\.){3}\\*))";
    public static final String REGX_IP_SEG = "(" + REGX_IP + "\\-" + REGX_IP + ")";

    private IPUtils() {}

    /**
     * 获取客户端IP
     */
    public static String getIpAddr() {
        return getIpAddr(ServletUtils.getRequest());
    }

    /**
     * 获取客户端IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null)
            return "unknown";

        String ip = request.getHeader("x-forwarded-for");
        if (isUnknown(ip))
            ip = request.getHeader("Proxy-Client-IP");
        if (isUnknown(ip))
            ip = request.getHeader("X-Forwarded-For");
        if (isUnknown(ip))
            ip = request.getHeader("WL-Proxy-Client-IP");
        if (isUnknown(ip))
            ip = request.getHeader("X-Real-IP");
        if (isUnknown(ip))
            ip = request.getRemoteAddr();

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 检查是否为内部IP地址
     */
    public static boolean internalIp(String ip) {
        byte[] addr = textToNumericFormatV4(ip);
        return internalIp(addr) || "127.0.0.1".equals(ip);
    }

    /**
     * 检查是否为内部IP地址
     */
    private static boolean internalIp(byte[] addr) {
        if (StringUtils.isNull(addr) || addr.length < 2)
            return true;

        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        // 172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;

        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4)
                    return true;
                break;
            case SECTION_5:
                if (b1 == SECTION_6)
                    return true;
                break;
            default:
                return false;
        }
        return false;
    }

    /**
     * 将IPv4地址转换成字节
     */
    public static byte[] textToNumericFormatV4(String text) {
        if (text == null || text.isEmpty())
            return null;

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);

        try {
            long l;
            int i;
            switch (elements.length) {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if (l < 0L || l > 4294967295L)
                        return null;
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if (l < 0L || l > 255L)
                        return null;
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if (l < 0L || l > 16777215L)
                        return null;
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if (l < 0L || l > 255L)
                            return null;
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if (l < 0L || l > 65535L)
                        return null;
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if (l < 0L || l > 255L)
                            return null;
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return bytes;
    }

    /**
     * 获取本地IP地址
     */
    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    /**
     * 获取主机名
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "未知";
        }
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip) {
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return StringUtils.substring(ip, 0, 255);
    }

    /**
     * 检测给定字符串是否为未知
     */
    public static boolean isUnknown(String checkString) {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 是否为IP
     */
    public static boolean isIP(String ip) {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP);
    }

    /**
     * 是否为IP通配符地址
     */
    public static boolean isIpWildCard(String ip) {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP_WILDCARD);
    }

    /**
     * 检测参数是否在ip通配符里
     */
    public static boolean ipIsInWildCardNoCheck(String ipWildCard, String ip) {
        String[] s1 = ipWildCard.split("\\.");
        String[] s2 = ip.split("\\.");
        boolean isMatchedSeg = true;
        for (int i = 0; i < s1.length && !s1[i].equals("*"); i++) {
            if (!s1[i].equals(s2[i])) {
                isMatchedSeg = false;
                break;
            }
        }
        return isMatchedSeg;
    }

    /**
     * 是否为IP段字符串，如: "10.10.10.1-10.10.10.99"
     */
    public static boolean isIPSegment(String ipSeg) {
        return StringUtils.isNotBlank(ipSeg) && ipSeg.matches(REGX_IP_SEG);
    }

    /**
     * 判断ip是否在指定网段中
     */
    public static boolean ipIsInNetNoCheck(String iparea, String ip) {
        int idx = iparea.indexOf('-');
        String[] sips = iparea.substring(0, idx).split("\\.");
        String[] sipe = iparea.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;

        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }

        if (ips > ipe) {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }

    /**
     * 校验ip是否符合过滤串规则
     *
     * @param filter 过滤IP列表，支持后缀'*'通配，支持网段如: `10.10.10.1-10.10.10.99`
     * @param ip     校验IP地址
     */
    public static boolean isMatchedIp(String filter, String ip) {
        if (StringUtils.isEmpty(filter) || StringUtils.isEmpty(ip))
            return false;

        String[] ips = filter.split(";");
        for (String iStr : ips) {
            if (isIP(iStr) && iStr.equals(ip))
                return true;
            else if (isIpWildCard(iStr) && ipIsInWildCardNoCheck(iStr, ip))
                return true;
            else if (isIPSegment(iStr) && ipIsInNetNoCheck(iStr, ip))
                return true;
        }
        return false;
    }
}
