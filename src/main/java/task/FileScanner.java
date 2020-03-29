package task;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author:wangxue
 * @date:2020/2/13 12:03
 */
public class FileScanner {

    //1.核心线程数；始终运行的线程数量（正式工）
    //2.最大线程数：有新任务，并且当前运行线程数小于最大线程数，会创建新线程来处理任务（正式工+临时工）
    //3-4.超过3这个数量，4这个时间单位，2-1（最大线程数-核心线程数这些线程就会关闭）
    //5.工作的阻塞队列
    //6.如果超出工作队列的长度，任务要处理的方式（4种策略）
   /* private ThreadPoolExecutor pool=new ThreadPoolExecutor(
            3,3,0, TimeUnit.MICROSECONDS,
            new LinkedBlockingDeque<>(),new ThreadPoolExecutor.CallerRunsPolicy()
    );*/

   //CallerRunsPolicy:谁指派的任务，让他自己去执行
    //AbortPolicy:抛出异常
    //DiscardPolicy丢弃最新的任务
    //DiscardOldestPolicy:丢弃最老的任务

   private ExecutorService pool=Executors.newFixedThreadPool(4);
    //之前多线程时创建线程池是一种快捷的方式

    //计数器，不传入数值表示初始化值为零
    private volatile AtomicInteger count=new AtomicInteger();

    // 线程等待的琐对象
    private Object lock=new Object();//第一种：synchronized（lock）进行等待

    private CountDownLatch latch=new CountDownLatch(1);//第2中实现：await（）阻塞等待直到latch==0
    private Semaphore semaphore=new Semaphore(0);//第三种实现，acquire（）阻塞等待一定数量的许可
    private ScanCallback callback;

    public FileScanner(ScanCallback callback) {
        this.callback=callback;
    }

    /**
     * 扫描文件目录
     * 最开始，不知道有多少子文件夹，不知道应该启动多少个线程数
     * @param path
     */
    public void scan(String path) {
        count.incrementAndGet();//启动根目录扫描任务，计数器++i操作
        doScan(new File(path));//根目录

    }

    /**
     *
     * @param dir 待处理的文件
     */
    private void doScan(File dir){
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.callback(dir);
                    File[] children=dir.listFiles();//下一级文件和文件夹
                    if(children!=null){
                        for(File child:children){
                            if(child.isDirectory()){//如果是文件夹，需要递归处理
                                //System.out.println("文件夹："+child.getPath());
                                count.incrementAndGet();//启动子文件夹扫描任务，计数器++i操作
                                System.out.println("当前任务数："+count.get());
                                doScan(child);
                            }
                            /*else{//如果是文件，代做的工作
                                //TODO
                                System.out.println("文件："+child.getPath());
                            }*/
                        }
                    }
                } finally {//保证线程计数不管是否出现异常，都能够进行-1操作
                    int r=count.decrementAndGet();
                    if(r==0){
                        /*第一种
                        synchronized (lock){
                            lock.notify();
                        }*/
                        //第二种实现
                        //latch.countDown();//默认-1
                        semaphore.release();//颁发许可证
                    }
                }
            }
        });
    }
    /**
     * 等待扫描任务结束（scan方法）
     * 多线程的任务等待
     * 1.join()：需要使用线程Thread类的引用对象
     * 2.wait()线程间的等待，
     */
    public void waitFinish() throws InterruptedException {
        /*
        synchronized (lock){
            lock.wait();
        }*/
        //latch.await();//默认==0向下执行，否则阻塞
        try {
            semaphore.acquire();//无参默认是请求1个，只有等待到一个，才会向下执行，否则阻塞
        }finally {
            System.out.println("关闭线程池");
            //线程池关闭：
            // shutdown：新传入的任务不再接收，但是目前所有的任务（所有线程中执行的任务+工作队列中的任务）还要执行完毕
            //shutdownNow：
            // 1.新传入的任务不再接收
            // 2.目前的任务（所有线程中执行的任务）判断是否能够停止，如果能够停止就结束任务，如果不能就执行完毕再停止线程
            // 3.工作队列中的任务是直接丢弃。
            pool.shutdownNow();
        }
    }

    public void shutdown(){
        System.out.println("线程池关闭");
        //两种关闭线程池的方式内部实现原理是通过内部Thread.interrupt()来中断
        //pool.shutdown();
        pool.shutdownNow();
    }
    public static void main(String[] args) throws InterruptedException {
/*        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        });
        t1.start();*/
        Object obj=new Object();
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                synchronized (obj){
                    obj.notify();
                }
            }
        });
        t2.start();
        //t2.join();
        synchronized (obj){
            obj.wait();
        }
        System.out.println(Thread.currentThread().getName());
    }
}
