package com.my.bysj.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class init implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext con=sce.getServletContext();
        con.setAttribute("status","0");
        System.out.println("开始Socket连接......");
        initialThread t1=new initialThread(sce);
        t1.start();
        System.out.println("进入服务器......");
    }

    class initialThread extends Thread{
        public ServletContextEvent sce=null;
        public initialThread(ServletContextEvent sce) {
            this.sce=sce;
        }

        @Override
        public void run() {
            try {
                // 创建服务端socket
                ServerSocket serverSocket = new ServerSocket(8000);

                // 创建客户端socket
                Socket socket = new Socket();

                //循环监听等待客户端的连接
                while(true){
                    // 监听客户端
                    socket = serverSocket.accept();

                    ServerThread thread = new ServerThread(socket,sce);
                    thread.start();

                    InetAddress address=socket.getInetAddress();
                    System.out.println("当前客户端的IP："+address.getHostAddress());
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    class ServerThread extends Thread{

        private Socket socket = null;
        public ServletContextEvent sce=null;
        public ServerThread(Socket socket,ServletContextEvent sce) {
            this.socket = socket;
            this.sce=sce;
        }

        @Override
        public void run() {
            InputStream is=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            OutputStream os=null;
            BufferedWriter bw=null;
            try {
                is = socket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                ServletContext con=sce.getServletContext();
                os = socket.getOutputStream();
                while(true){
                    String flag= (String) con.getAttribute("status");
                    if(flag.equals("1")){
                        os.write("cqupt".getBytes());
                        con.setAttribute("status","0");
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            } finally{
                //关闭资源
                try {
                    if(bw!=null)
                        bw.close();
                    if(os!=null)
                        os.close();
                    if(br!=null)
                        br.close();
                    if(isr!=null)
                        isr.close();
                    if(is!=null)
                        is.close();
                    if(socket!=null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
