import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author:wangxue
 * @date:2020/2/14 10:27
 */
public class ThreadTest {
    //多线程下线程安全的计数器
    private static volatile AtomicInteger COUNT=new AtomicInteger();
    public static void main(String[] args){
        for(int i=0;i<20;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int j=0;j<1000;j++) {
                        COUNT.incrementAndGet();//先++，再获取
                    }
                }
            }).start();
        }
        while(Thread.activeCount()>1){
            Thread.yield();
        }
        System.out.println(COUNT.get());
    }
}
