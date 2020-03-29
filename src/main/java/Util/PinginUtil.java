package Util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author:wangxue
 * @date:2020/1/9 19:51
 */
public class PinginUtil {
    private static final HanyuPinyinOutputFormat FORMAT=new HanyuPinyinOutputFormat();
    /**
     * 中文字符格式
     */
    private static final String CHINESE_PATTERN = "[\\u4E00-\\u9FA5]";
    static{
        //设置拼音小写
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //设置不带音调
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //设置带v字符，如绿lv
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 字符串是否包含中文
     * @param name
     * @return
     */
    public static boolean contaninChinese(String name){
        //".*"表示匹配任意字符串
        return name.matches(".*"+CHINESE_PATTERN +".*");
    }

    /**
     * 获取字符串组合
     * @param name
     * @return
     */
    public static String[] get(String name) {
        String[] result=new String[2];
        StringBuilder pinyin=new StringBuilder();
        StringBuilder pinyinFirst=new StringBuilder();
        for(char c:name.toCharArray()){
            try {
                //如果不是拼音，比如特殊字符直接加入即可
                String[] pinyins=PinyinHelper.toHanyuPinyinStringArray(c,FORMAT);
                if(pinyins==null||pinyins.length==0){
                    pinyin.append(c);
                    pinyinFirst.append(c);
                }else{
                    pinyin.append(pinyins[0]);
                    pinyinFirst.append(pinyins[0].charAt(0));
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        result[0]=pinyin.toString();
        result[1]=pinyinFirst.toString();
        return result;
    }

    /**
     * 和[he,hu,huo,...]
     * @param name 文件名
     * @param fullSpell true表示全拼，false表示拼音首字母
     * @return 包含多音字的字符串组合
     */
    public static String[][] get(String name,boolean fullSpell){
        char[] chars=name.toCharArray();
        String[][] result=new String[chars.length][];
        for(int i=0;i<chars.length;i++){
            try {
                String[] pinyins=PinyinHelper.toHanyuPinyinStringArray(chars[i],FORMAT);
                if(pinyins==null || pinyins.length==0){
                    result[i]=new String[]{String.valueOf(chars[i])};
                }else{
                    result[i]=unique(pinyins,fullSpell);
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        return result;
    }

    /**
     * [he,hu,huo...][zhang,chang][he,hu,huo..]
     * 每个中文字符返回拼音是字符串数组，每两个字符串数组合并为一个字符串数组，之后依次类推
     * @param pinyinArray 将每个字符的拼音组合起来
     */
    public static String[] compose(String[][] pinyinArray){
        if(pinyinArray==null){
            return null;
        }else if(pinyinArray.length==0){
            return null;
        }else if(pinyinArray.length==1){
            return pinyinArray[0];
        }else{
            for(int i=1;i<pinyinArray.length;i++){
                pinyinArray[0]=compose(pinyinArray[0],pinyinArray[i]);
            }
        }
        return pinyinArray[0];
    }

    /**
     * 合并两个拼音数组为一个
     * @param pinyins1
     * @param pinyins2
     * @return
     */
    public static String[] compose(String[] pinyins1,String[] pinyins2){
        String[] result=new String[pinyins1.length * pinyins2.length];
        for(int i=0;i<pinyins1.length;i++){
            for(int j=0;j<pinyins2.length;j++){
                result[i*pinyins2.length+j]=pinyins1[i]+pinyins2[j];
            }
        }
        return result;
    }

    /**
     * 去重
     * @param array
     * @param fullSpell
     * @return
     */
    public static String[] unique(String[] array,boolean fullSpell){
        Set<String> set=new HashSet<>();
        for(String s:array){
            if(fullSpell){
                set.add(s);
            }else{
                set.add(String.valueOf(s.charAt(0)));
            }
        }
        return set.toArray(new String[set.size()]);
    }
    public static void main(String[] args) {
        System.out.println(Arrays.toString(get("中华人民共和国")));
        System.out.println(Arrays.toString(get("中华1人b民共和国")));

        System.out.println(Arrays.toString(compose(get("和长和",true))));
        System.out.println(Arrays.toString(compose(get("和长和",false))));
    }
}
