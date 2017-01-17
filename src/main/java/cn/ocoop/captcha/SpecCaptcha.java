package cn.ocoop.captcha;

import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import static cn.ocoop.captcha.Randoms.num;


public class SpecCaptcha extends Captcha {
    public SpecCaptcha() {
    }

    public SpecCaptcha(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public SpecCaptcha(int width, int height, int len) {
        this(width, height);
        this.len = len;
    }

    public SpecCaptcha(int width, int height, int len, Font font) {
        this(width, height, len);
        this.font = font;
    }

    /**
     * 生成验证码
     *
     * @throws IOException IO异常
     */
    @Override
    public void out(OutputStream out) throws IOException {
        try {
            graphicsImage(out);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 画随机码图
     *
     * @param out 输出流
     */
    private void graphicsImage(OutputStream out) throws IOException {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        AlphaComposite ac3;
        Color color;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 随机画干扰的蛋蛋
        for (int i = 0; i < 15; i++) {
            color = color(150, 250);
            g.setColor(color);
            g.drawOval(num(width), num(height), 5 + num(10), 5 + num(10));// 画蛋蛋，有蛋的生活才精彩
        }
        g.setFont(font);
        int h = height - ((height - font.getSize()) >> 1),
                w = width / getLen(),
                size = w - font.getSize() + 1;
            /* 画字符串 */
        for (int i = 0; i < getLen(); i++) {
            ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);// 指定透明度
            g.setComposite(ac3);
            color = new Color(20 + num(110), 20 + num(110), 20 + num(110));// 对每个字符都用随机颜色
            g.setColor(color);
            g.drawString(getChars()[i] + "", (width - (getLen() - i) * w) + size, h - 4);
        }
        ImageIO.write(bi, "png", out);
        out.flush();
    }
}
