import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @author:wangxue
 * @date:2020/2/14 11:11
 */
//多线程的高阶部分，线程等待
public class WaitTest {
    /**
     * 等待所有线程执行完毕
     * 1.CountDownLatch 初始化一个数组，可以对数值进行countDown（）i--操作，会一直等待await（）到i==0
     * 2.Semaphone release()进行一定数量许可证的颁发，acquire（）阻塞并等待一定数量的许可
     * 相对来说，semaphore功能更强大，也更灵活一点
     * @param args
     */
    private static int COUNT=5;
    private static CountDownLatch LATCH=new CountDownLatch(COUNT);
    private static Semaphore  SEMAPHORE=new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {
        for(int i=0;i<COUNT;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                    //LATCH.countDown();//i--
                    SEMAPHORE.release();//颁发一定数量的许可证。无参代表颁发一个数量的许可证
                }
            }).start();
        }
        //main在所有子线程执行完毕之后，再运行以下代码
        //LATCH.await();//await()会阻塞一直等待，知道LATCH的值==0
        SEMAPHORE.acquire(5);//无参代表请求资源数量为1，也可以请求指定数量的资源
        System.out.println(Thread.currentThread().getName());
    }

}
