package Util;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import task.DBInit;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author:wangxue
 * @date:2020/2/12 23:08
 */
public class DBUtil {
    //volatile保证可见性，及禁止指令重排序
    private static volatile DataSource DATA_SOURCE;

    /**
     * 提供获取数据库连接池的功能
     * 使用单例模式（多线程安全版本）
     * 多线程：可见性（主内存拷贝到工作内存），原子性，有序性
     * @return
     */
    private static DataSource getDataSource(){
        //单例模式表述：双层校验，判断变量是否==null      中间加入synchronized，多线程的安全性，防止多次进入初始化变量
        if(DATA_SOURCE==null){//目的：提高效率
            synchronized (DBUtil.class){
                if(DATA_SOURCE==null){
                    //做初始化操作，使用volatile关键字进制指令重排序，建立内存屏障
                    //为什么选用SQLite数据库：1.因为SQLite是一个包，直接嵌入到项目中 2.因为SQLite比较小，方便使用，而像MySQL比较大，并且依赖于操作系统
                    SQLiteConfig config=new SQLiteConfig();
                    config.setDateStringFormat(Util.DATA_PATTERN);
                    //会有以下的步骤：1，分配对象的内存空间 2，初始化对象 3，将对象赋值给引用
                    //重排序后的顺序：1,3,2     当一个线程1执行到3时，已经分配给内存空间，线程2执行到第一个DataSource==null，判断不为空，直接return DataSource，然后之后DataSource的引用就会有问题，因为还没有初始化对象
                    DATA_SOURCE=new SQLiteDataSource(config);
                    ((SQLiteDataSource)DATA_SOURCE).setUrl(getUrl());
                }
            }
        }
        return DATA_SOURCE;
    }

    /**
     * 获取sqlite数据库文件url的方法
     * @return
     */
    public  static String getUrl(){
        //获取target编译文件夹的路径
        try {
            URL classesURL= DBInit.class.getClassLoader().getResource("./");
            //获取target/classes文件夹的父目录路径
            String dir=new File(classesURL.getPath()).getParent();
            String url="jdbc:sqlite://"+dir+File.separator+"everything-like.db";
            url= URLDecoder.decode(url,"UTF-8");
            System.out.println("获取数据库文件路径："+url);
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("获取数据库文件路径失败",e);
        }
    }
    /**
     * 提供获取数据库连接的方法
     * 从数据库连接池DataSource.getConnection()来获取数据库连接
     * @return
     */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(getConnection());
    }

    public static void close(Connection connection, Statement statement) {
        close(connection,statement,null);
    }

    /**
     * 释放数据库资源
     * @param connection 数据库连接
     * @param statement sql执行对象
     * @param resultSet 结果集
     */
    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if(connection!=null){
                connection.close();
            }
            if(statement!=null){
                statement.close();
            }
            if(resultSet!=null){
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("释放数据库资源错误");
        }
    }
}
