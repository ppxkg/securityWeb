package com.my.bysj.activity.web.controller;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.my.bysj.util.DateTimeUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
public class test {
    public static void main(String[] args) {
        String data=DateTimeUtil.getSysTime();
        System.out.println(data);
    }
}

