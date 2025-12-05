package com.junoyi.framework.stater.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JunoYi配置参数类
 *
 * @author Fan
 */
@Component
@ConfigurationProperties(prefix = "junoyi")
public class JunoYiProperties {

    /**
     * JunoYi 框架版本
     */
    private String version;

    /**
     * 服务器名称
     */
    private String name;

    /**
     * 版权年号
     */
    private String copyrightYear;

    /**
     * 版权归属
     */
    private String copyright;

    /**
     * 网站备案号
     */
    private String registration;

    /**
     * 服务器端口号
     */
    private int port;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getCopyrightYear(){
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear){
        this.copyrightYear = copyrightYear;
    }

    public String getCopyright(){
        return copyright;
    }

    public void setCopyright(String copyright){
        this.copyright = copyright;
    }

    public String getRegistration(){
        return registration;
    }

    public void setRegistration(String registration){
        this.registration = registration;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

}