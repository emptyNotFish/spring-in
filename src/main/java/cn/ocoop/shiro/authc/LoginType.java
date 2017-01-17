package cn.ocoop.shiro.authc;

public enum LoginType {
    USERNAME_PASSWORD("username_password", "用户名密码"),
    CAPTCHA("captcha", "手机验证码"),
    WX("wx", "微信自动登录"),
    UNION("union", "第三方接口");
    private String type;
    private String value;

    LoginType(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(String type) {
        return this.getType().equals(type);
    }
}