package app;

import Util.PinginUtil;
import Util.Util;

import java.io.File;
import java.util.Date;
import java.util.Objects;

public class FileMeta {
    private String name;//文件名称
    private String path;//文件所在父目录
    private Long size;//文件大小
    private Date lastModified;//文件上次修改时间
    private Boolean isDirectory;
    private String sizeText;//给客户端控件使用，和app.fxml定义的名称要一致
    private String lastModifiedText;//和app.fxml定义的一致
    private String pinyin;
    private String pinyinFirst;

    //通过文件设置属性
    public FileMeta(File file){
        this(file.getName(),file.getParent(),file.isDirectory(),file.length(),new Date(file.lastModified()));
    }
    //通过数据库获取获取的数据设置FileMeta
    public FileMeta(String name,String path,Boolean isDirectory,long size,Date lastModified){
        this.name=name;
        this.path=path;
        this.isDirectory=isDirectory;
        this.size=size;
        this.lastModified=lastModified;
        if(PinginUtil.contaninChinese(name)){
            String[] pinyins=PinginUtil.get(name);
            pinyin=pinyins[0];
            pinyinFirst=pinyins[1];
        }
        //客户端控件文件大小，文件上次修改时间的设置
        sizeText=Util.parseSize(size);
        lastModifiedText=Util.parseDate(lastModified);

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getSizeText() {
        return sizeText;
    }

    public void setSizeText(String sizeText) {
        this.sizeText = sizeText;
    }

    public String getLastModifiedText() {
        return lastModifiedText;
    }

    public void setLastModifiedText(String lastModifiedText) {
        this.lastModifiedText = lastModifiedText;
    }


    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinFirst() {
        return pinyinFirst;
    }

    public void setPinyinFirst(String pinyinFirst) {
        this.pinyinFirst = pinyinFirst;
    }

    public Boolean getDirectory() {
        return isDirectory;
    }

    public void setDirectory(Boolean directory) {
        isDirectory = directory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMeta fileMeta = (FileMeta) o;
        return Objects.equals(name, fileMeta.name) &&
                Objects.equals(path, fileMeta.path) &&
                Objects.equals(isDirectory, fileMeta.isDirectory);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, path, isDirectory);
    }

    @Override
    public String toString() {
        return "FileMeta{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", isDirectory=" + isDirectory +
                '}';
    }
}
