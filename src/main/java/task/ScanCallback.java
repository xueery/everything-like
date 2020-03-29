package task;

import java.io.File;

/**
 * @author:wangxue
 * @date:2020/2/14 11:49
 */
public interface ScanCallback {
    //对于文件夹的扫描进行
    void callback(File dir);
}
