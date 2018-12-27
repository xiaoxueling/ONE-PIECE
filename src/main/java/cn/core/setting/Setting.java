package cn.core.setting;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.util.StringHelper;

@Service
public class Setting {

	@Autowired
	SqlSession sqlSession;
	
	private static Map<String, Object> map=new ConcurrentHashMap<String, Object>();
 	
	public <TSetting>TSetting getSetting(Class<TSetting>setting){
		return this.getSetting(setting,null);
	}
	
	public <TSetting> TSetting getSetting(Class<TSetting> setting,Integer shopId) {

		String key = getSettingKey(setting,shopId);
		TSetting tSetting = null;
		try {
			tSetting = setting.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(map.containsKey(key)){
			return  (TSetting)map.get(key);
		}
		
		SettingEntity entity = getSettingEntity(key);
		if (entity != null && !StringHelper.IsNullOrEmpty(entity.getSetting())) {
			ObjectMapper objectMapper = new ObjectMapper();
			String v = entity.getSetting();
			try {
				tSetting =objectMapper.readValue(v,setting);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		map.put(key, tSetting);
		
		return tSetting;
	}

	public <TSetting> boolean setSetting(TSetting setting){
		return this.setSetting(setting, null);
	}
	
	public <TSetting> boolean setSetting(TSetting setting,Integer shopId) {
		boolean result = false;

		if (setting == null) {
			return result;
		}

		try {
			Class<?> settingClass = setting.getClass();
			String key = getSettingKey(settingClass, shopId);

			SettingEntity entity = getSettingEntity(key);
			if (entity == null) {
				entity = new SettingEntity(key);
			}

			ObjectMapper objectMapper=new ObjectMapper();
			
			entity.setSetting(objectMapper.writeValueAsString(setting));

			// 保存
			if (entity.getId() > 0) {
				result = sqlSession.update("SystemSettingDao.updateSetting",entity) > 0;
			} else {
				result = sqlSession.insert("SystemSettingDao.insertSetting",entity) > 0;
			}
			if(result){
				map.put(key, setting);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * 获取entity
	 * 
	 * @param setting
	 * @return
	 */
	public SettingEntity getSettingEntity(String key) {

		SettingEntity entity = new SettingEntity();

		// 根据key获取 settingEntity
		entity = sqlSession.selectOne("SystemSettingDao.selectSettingByKey",key);

		return entity;
	}

	public <TSetting> String getSettingKey(Class<TSetting> setting,Integer shopId) {
		String key = setting.getSimpleName();
		if(shopId!=null&&shopId>0){
			key += "_" + shopId;
		}
		return key;
	}
}
