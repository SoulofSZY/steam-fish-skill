/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ZXingTest
 * Author:   15496
 * Date:     2019/6/26 23:18
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szy.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * 〈一句话功能简述〉<br>
 * 〈google.zxing 生成二维码图片〉
 *
 * @author 15496
 * @create 2019/6/26
 * @since 1.0.0
 */
public class ZXingTest {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int MARGIN = 0;
    private static final int LOGOPART = 4;


    /**
     * 生成二维码矩阵信息
     *
     * @param content 二维码图片内容
     * @param width   二维码图片宽度
     * @param height  二维码图片高度
     * @return
     */
    public static BitMatrix setBitMatrix(String content, int width, int height) {
        Hashtable<EncodeHintType, Object> hits = new Hashtable<EncodeHintType, Object>();
        // 指定编码方式 防止中文乱码
        hits.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hits.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 指定二维码四周白色区域大小
        hits.put(EncodeHintType.MARGIN, MARGIN);

        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hits);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitMatrix;
    }


    public static void write2File(BitMatrix bitMatrix, String format, OutputStream ops, String logoPath) throws IOException {
        BufferedImage image = toBufferedImage(bitMatrix);

        // 添加logo
        if (null != logoPath && !"".equals(logoPath.trim())) {
            addLogo(image, logoPath);
        }

        ImageIO.write(image, format, ops);
    }

    /**
     * 生成二维码图片
     *
     * @param bitMatrix 二维码矩阵
     * @return
     */
    public static BufferedImage toBufferedImage(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
            }
        }

        return image;
    }


    public static BufferedImage addLogo(BufferedImage image, String logoPath) throws IOException {
        Graphics2D g = image.createGraphics();
        BufferedImage logoImage = ImageIO.read(new File(logoPath));

        // 计算logo图片大小， 根据短边生成正方形
        int width = image.getWidth() < image.getHeight() ? image.getWidth() / LOGOPART : image.getHeight() / LOGOPART;
        int height = width;
        // 计算logo图片摆放位置
        int x = (image.getWidth() - width) / 2;
        int y = (image.getHeight() - height) / 2;
        // 在二维码图片上绘制logo图片
        g.drawImage(logoImage, x, y, width, height, null);
        // 绘制logo边框
        //g.drawRoundRect(x, y, logoImage.getWidth(), logoImage.getHeight(), 10, 10);
        // 画笔粗细
        g.setStroke(new BasicStroke(2));
        // 边框颜色
        g.setColor(Color.white);
        // 矩形边框
        g.drawRect(x, y, width, height);
        logoImage.flush();
        g.dispose();
        return image;
    }


    public static void main(String[] args) {
        String content = "http://www.baidu.com";
        String logoPath = "D:\\temp\\123.jpeg";
        String format = "png";
        int width = 180;
        int height = 180;
        BitMatrix bitMatrix = setBitMatrix(content, width, height);

        try (OutputStream ops = new FileOutputStream("D:\\temp\\target\\" + System.currentTimeMillis() + ".png")) {
            write2File(bitMatrix, format, ops, logoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
