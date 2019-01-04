package cn.util.excel.test;

import cn.util.excel.ExcelUtils;
import cn.util.excel.SheetData;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: @baoxuechao
 * Date: 2019/1/4 16:17
 * Content: 模板导出测试类
 */
public class TestDownloadModel {

    public static void main(String[] args, HttpServletResponse response) {
        //创建数据sheet
        SheetData sd = new SheetData("测试模板");
        //TODO 单一值放入sd.put();
        sd.put("stockName","测试库房");
        //查询商品集合
        List<Map> itemlist = new ArrayList<Map>();
        for(Map item:itemlist) {
            //TODO 循环遍历值放入data中
            sd.addData(item);
        }
        ExcelUtils.writeData("测试模板.xlsx", "测试模板",response ,sd);
    }

}
