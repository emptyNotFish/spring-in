package cn.ocoop.redis;


public class RedisConfig {
    private String host;
    private Integer port;
    private Integer timeout;
    private String password;

    public RedisConfig(String host, Integer port, String password, Integer timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.password = password;
    }

    public RedisConfig() {

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}