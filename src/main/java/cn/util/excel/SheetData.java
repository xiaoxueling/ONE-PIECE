package cn.util.excel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: @baoxuechao
 * Date: 2018/10/12 13:32
 * Content: sheet页数据定制
 */
public class SheetData {

    /**
     * sheet页中存储 #{key} 的数据
     */
    private Map<String, Object> map = new HashMap<String, Object>();

    /**
     * 列表数据存储 sheet页中替换${key} 并以列为单位向下赋值
     */
    private List<Object> datas = new LinkedList<Object>();

    private String name ;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public SheetData(String name) {
        super();
        this.name = name;
    }

    public void put(String key , Object value) {
        map.put(key, value);
    }

    public void remove(String key) {
        map.remove(key);
    }

    public Object get(String key) {
        return map.get(key);
    }

    /**
     * 清理map存储和数据存储
     */
    public void clear() {
        map.clear();
        datas.clear();
    }

    public void addData(Object t){
        datas.add(t);
    }

    public void addDatas(List<? extends Object> list) {
        datas.addAll(list);
    }

    public List<Object>  getDatas() {
        return datas;
    }

}
