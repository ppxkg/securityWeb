package com.my.bysj.activity.web.controller;

import com.my.bysj.activity.dao.PersonDao;
import com.my.bysj.activity.domain.Bulletin;
import com.my.bysj.activity.domain.Person;
import com.my.bysj.activity.domain.User;
import com.my.bysj.activity.service.UserService;
import com.my.bysj.activity.service.impl.UserServiceImpl;
import com.my.bysj.activity.service.impl.personServiceImpl;
import com.my.bysj.activity.service.personService;
import com.my.bysj.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.my.bysj.util.PrintJson.printJsonFlag;
import static com.my.bysj.util.PrintJson.printJsonObj;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class PersonController extends HttpServlet {
    PersonDao personDao = SqlSessionUtil.getSqlSession().getMapper(PersonDao.class);
    public static File convertBase64ToFile(String fileBase64String, String filePath, String fileName) {

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bfile = decoder.decodeBuffer(fileBase64String);

            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void service(HttpServletRequest requst, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("欢迎进入用户管理系统！");
        String path=requst.getServletPath();
        response.setCharacterEncoding("utf-8");
        //通过响应头，设置浏览器也使用utf-8字符集
        response.setHeader("Content-Type","text/html;charset=UTF-8");
        if("/activity/Person/save.do".equals(path)){
            savePerson(requst,response);
        }else if("/toLogin.do".equals(path)){
            login(requst,response);
        }else if("/toRegister.do".equals(path)){
            register(requst,response);
        }
        else if("/getBulletin.do".equals(path)){
            getBulletin(requst,response);
        }else if("/setBulletin.do".equals(path)){
            setBulletin(requst,response);
        }
        else if("/getPerson.do".equals(path)){
            getPerson(requst,response);
        }
        else if("/getCsv.do".equals(path)){
            getCsv(requst,response);
        }
        else if("/saveImg.do".equals(path)){
            saveImg(requst,response);
        }
        else if("/getImg.do".equals(path)){
            getImg(requst,response);
        }
        else if("/setStatus.do".equals(path)){
            setStatus(requst,response);
        }
    }

    private void setStatus(HttpServletRequest requst, HttpServletResponse response) {
        System.out.println("正在开门...");
        String status=requst.getParameter("status");
        ServletContext con=getServletContext();
        con.setAttribute("status",status);
    }

    private void getImg(HttpServletRequest requst, HttpServletResponse response) {
        String dirName="C:\\Users\\27643\\Desktop\\securityWeb\\src\\main\\webapp\\image";
        File file = new File(dirName);
        ArrayList<String> data=new ArrayList<>();
        if(file.isDirectory()) {
            System.out.println("正在读取"+dirName+"目录....");
            String[] list = file.list();
            for(int i=0;i<list.length;i++) {
                File file2 = new File(dirName+"\\"+list[i]);
                if(file2.isDirectory()) {
                    System.out.println("文件夹："+list[i]);
                }else {
                    System.out.println("文件："+list[i]);
                }
            }
            for(int i=0;i<list.length;i++){
                data.add(list[i]);
            }
            printJsonObj(response,data);
        }else {
            System.out.println(dirName+"不是一个目录。");
        }
    }

    private void saveImg(HttpServletRequest requst, HttpServletResponse response) {
        String time=DateTimeUtil.getSysTime();
        String imgBase64=requst.getParameter("img");
        convertBase64ToFile(imgBase64,"C:\\Users\\27643\\Desktop\\securityWeb\\src\\main\\webapp\\image\\",time+".png");
        System.out.println("上传图片！");
    }

    private void getCsv(HttpServletRequest requst, HttpServletResponse response) {
        try {
            response.sendRedirect("C:\\Users\\27643\\Desktop\\Dlib_face\\data\\features_all.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getPerson(HttpServletRequest requst, HttpServletResponse response) {
        System.out.println("正在获取进出人员名单...");
        personService ps= (personService) ServiceFactory.getService(new personServiceImpl());
        List<Person> persons=ps.getPerson();
        printJsonObj(response,persons);
    }

    private void setBulletin(HttpServletRequest requst, HttpServletResponse response) {
        System.out.println("正在发布公告...");
        String id=UUIDUtil.getUUID();
        String data=requst.getParameter("data");
        String item=requst.getParameter("item");
        String time=DateTimeUtil.getSysTime();
        Bulletin bulletin=new Bulletin(id,data,time,item);
        UserService us= (UserService) ServiceFactory.getService(new UserServiceImpl());
        boolean flag=us.setBulletin(bulletin);
        printJsonFlag(response,flag);
    }

    private void getBulletin(HttpServletRequest requst, HttpServletResponse response) {
        System.out.println("正在获取公告信息...");
        UserService us= (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<Bulletin> bulletins=us.getBulletin();
        printJsonObj(response,bulletins);
    }

    private void register(HttpServletRequest requst, HttpServletResponse response) {
        System.out.println("注册操作。。。");
        String username=requst.getParameter("username");
        String usernum=requst.getParameter("usernum");
        String password=requst.getParameter("password");
        User user=new User();
        user.setUsername(username);
        user.setUsernum(usernum);
        user.setPassword(password);
        UserService us= (UserService) ServiceFactory.getService(new UserServiceImpl());
        int status=us.register(user);
        printJsonObj(response,status);
    }

    private void login(HttpServletRequest requst, HttpServletResponse response) {
        System.out.println("进入登录查询！");
        int status=0;
        String usernum=requst.getParameter("usernum");
        String password=requst.getParameter("password");
        User user=new User();
        user.setUsernum(usernum);
        user.setPassword(password);
        UserService us= (UserService) ServiceFactory.getService(new UserServiceImpl());
        User user1=us.login(user);
        if(user1!=null){
            status=1;
        }
        printJsonObj(response,status);
    }

    private void savePerson(HttpServletRequest requst, HttpServletResponse response) {
        String id= UUIDUtil.getUUID();
        String name=requst.getParameter("name");
        String time=requst.getParameter("time");
        String temperature=requst.getParameter("temperature");
//        String address=requst.getParameter("address");
        Person person=new Person();
        person.setId(id);
        person.setName(name);
        person.setTime(time);
        person.setTemperature(temperature);
//        person.setAddress(address);
        personService ps= (personService) ServiceFactory.getService(new personServiceImpl());
        boolean flag=ps.savePerson(person);
        printJsonFlag(response,flag);
    }
}
