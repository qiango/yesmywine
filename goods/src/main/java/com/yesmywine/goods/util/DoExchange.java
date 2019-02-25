package com.yesmywine.goods.util;

import com.yesmywine.util.basic.ValueUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hz on 2017/5/5.
 */
public class DoExchange {

    ArrayList<ArrayList<String>> result;

    public List doChange(List arrayLists) {
        int len = arrayLists.size();
        //判断数组size是否小于2，如果小于说明已经递归完成了，否则你们懂得的，不懂？断续看代码
        if (len < 2) {
            return (ArrayList<ArrayList<String>>) arrayLists;
        }
        //拿到第一个数组
        int len0;
        if (arrayLists.get(0) instanceof String[]) {
            String[] arr0 = (String[]) arrayLists.get(0);
            len0 = arr0.length;
        } else {
            len0 = ((ArrayList<String>) arrayLists.get(0)).size();
        }

        //拿到第二个数组
        String[] arr1 = (String[]) arrayLists.get(1);
        int len1 = arr1.length;

        //计算当前两个数组一共能够组成多少个组合
        int lenBoth = len0 * len1;

        //定义临时存放排列数据的集合
        ArrayList<ArrayList<String>> tempArrayLists = new ArrayList<>(lenBoth);
        ArrayList<ArrayList<String>> reArrayLists = new ArrayList<>(lenBoth);

        //第一层for就是循环arrayLists第一个元素的
        for (int i = 0; i < len0; i++) {
            //第二层for就是循环arrayLists第二个元素的
            for (int j = 0; j < len1; j++) {
                //判断第一个元素如果是数组说明，循环才刚开始
                if (arrayLists.get(0) instanceof String[]) {
                    String[] arr0 = (String[]) arrayLists.get(0);
                    ArrayList<String> arr = new ArrayList<>();
                    arr.add(arr0[i]);
                    arr.add(arr1[j]);
                    //把排列数据加到临时的集合中
                    tempArrayLists.add(arr);
                } else {
                    //到这里就明循环了最少一轮啦，我们把上一轮的结果拿出来继续跟arrayLists的下一个元素排列
                    ArrayList<ArrayList<String>> arrtemp = (ArrayList<ArrayList<String>>) arrayLists.get(0);
                    ArrayList<String> arr = new ArrayList<>();
                    for (int k = 0; k < arrtemp.get(i).size(); k++) {
                        arr.add(arrtemp.get(i).get(k));
                    }
                    arr.add(arr1[j]);
                    tempArrayLists.add(arr);
                }
            }
        }

        //这是根据上面排列的结果重新生成的一个集合
        List newArrayLists = new ArrayList<>();
        //把还没排列的数组装进来，看清楚i=2的喔，因为前面两个数组已经完事了，不需要再加进来了
        for (int i = 2; i < arrayLists.size(); i++) {
            newArrayLists.add(arrayLists.get(i));
        }
        //记得把我们辛苦排列的数据加到新集合的第一位喔，不然白忙了
        newArrayLists.add(0, tempArrayLists);

        //你没看错，我们这整个算法用到的就是递归的思想。
        if(newArrayLists.size() > 1) {
            doChange(newArrayLists);
        }

        if( ValueUtil.isEmpity(result)||this.result.size() == 0){
            result = tempArrayLists;
        }

        return result;
    }
}
