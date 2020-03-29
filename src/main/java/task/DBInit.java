package task;

/**
 * @author:wangxue
 * @date:2020/2/12 23:11
 */

import Util.DBUtil;
import org.sqlite.core.DB;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * 1.数据库初始化
 * 调用DBUtil.getConnection()就可以完成数据库初始化
 * 2.并且读取sql文件
 * 3.再执行sql语句来初始化表
 */
public class DBInit {

    public static String[] readSQL(){
        try {
            //通过ClassLoader获取流，或者通过FileInpputStream获取
            InputStream is=DBInit.class.getClassLoader().getResourceAsStream("init.sql");
            //字节流转换为字符流：需要通过字节和字符转换流进行操作
            BufferedReader br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuilder sb=new StringBuilder();
            String line;
            while((line=br.readLine())!=null){
                if(line.contains("--")){//去除--注释的代码
                    line=line.substring(0,line.indexOf("--"));
                }
                sb.append(line);
            }
            String[] sqls=sb.toString().split(";");
            return sqls;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取sql文件错误",e);
        }
    }
    public static void init(){
        // 数据库JDBC操作，sql语句的执行
        Connection connection=null;
        Statement statement=null;
        try{
            // 1.建立数据库连接
            connection=DBUtil.getConnection();
            // 2.创建sql语句执行对象Statement
            statement=connection.createStatement();
            String[] sqls=readSQL();
            for(String sql:sqls){
                System.out.println("执行sql语句："+sql);
                // 3.执行sql语句
                statement.executeUpdate(sql);
            }
            // 4.如果是查询操作，获取结果集ResultSet，处理结果集
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("初始化数据库表操作失败");
        }finally {
            // 5.释放资源
            DBUtil.close(connection,statement);
        }
    }

    public static void main(String[] args) {
        String[] sqls=readSQL();
        for(String sql:sqls){
            System.out.println(sql);
        }
        init();
    }
}
