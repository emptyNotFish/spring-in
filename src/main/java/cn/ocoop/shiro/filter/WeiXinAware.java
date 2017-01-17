package cn.ocoop.shiro.filter;

public interface WeiXinAware {
    String getOpenId(String code);
}
