package cn.ocoop.captcha;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

import static cn.ocoop.captcha.Randoms.alpha;
import static cn.ocoop.captcha.Randoms.num;

public abstract class Captcha {
    protected Font font = new Font("Verdana", Font.ITALIC | Font.BOLD, 28);   // 字体
    protected int len = 5;  // 验证码随机字符长度
    protected int width = 150;  // 验证码显示跨度
    protected int height = 40;  // 验证码显示高度
    private char[] chars;

    public char[] getChars() {
        if (chars != null) return chars;
        chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = alpha();
        }
        return chars;
    }

    public void clearChars(char[] chars) {
        this.chars = null;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    protected Color color(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + num(bc - fc);
        int g = fc + num(bc - fc);
        int b = fc + num(bc - fc);
        return new Color(r, g, b);
    }

    public abstract void out(OutputStream os) throws IOException;

    public String getCaptcha() {
        return new String(getChars());
    }

}
