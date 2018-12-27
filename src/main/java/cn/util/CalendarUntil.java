package cn.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * 时间操作
 *
 */
public class CalendarUntil {

	private final static String DEFAULT_DATE="yyyy-MM-dd";
	private final static String DEFAULT_TIME="HH:mm:ss";
	private final static String DEFAULT_DATETIME="yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 日期转字符串
	 * 
	 * @param date
	 *            日期 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String ToDateString() {
		return ToDateString(new Date());
	}

	/**
	 * 日期转字符串
	 * 
	 * @param date
	 *            日期 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String ToDateString(Date date) {

		return ToDateString(date, DEFAULT_DATETIME);
	}

	/**
	 * 日期转字符串
	 * 
	 * @param date
	 *            日期
	 * @param format
	 *            格式化的格式 默: yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String ToDateString(Date date, String format) {
		if (date == null) {
			date = new Date();
		}
		if (format == null || StringUtils.isEmpty(format)) {
			format = DEFAULT_DATETIME;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);

		return dateFormat.format(date);
	}

	/**
	 * object转时间
	 * 
	 * @param Object
	 * @return date
	 */
	public static Date ParseDate(Object obj) {
		if (obj == null) {
			return null;
		}
		return ParseDate(obj,"", null);
	}

	/**
	 * object转时间
	 * 
	 * @param Object
	 * @param format
	 * @return date
	 */
	public static Date ParseDate(Object obj, String format) {
		if (obj == null) {
			return null;
		}
		return ParseDate(obj, format,null);
	}
	
	/**
	 * object转时间
	 * 
	 * @param Object
	 * @param defaultDate
	 * @return date
	 */
	public static Date ParseDate(Object obj, Date defaultDate) {
		if (obj == null) {
			return null;
		}
		return ParseDate(obj,"",defaultDate);
	}
	
	/**
	 * 时间字符串转时间
	 * 
	 * @param Object
	 * @param format
	 * @param defaultDate
	 * @return date
	 */
	public static Date ParseDate(Object obj, String format, Date defaultDate) {
		
		String dateStr=DataConvert.ToString(obj);
		if(StringHelper.IsNullOrEmpty(dateStr)){
			return defaultDate;
		}
		if(StringHelper.IsNullOrEmpty(format)){
			format =DEFAULT_DATETIME;
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return  dateFormat.parse(dateStr);
		} catch (ParseException e) {
			
		}
		return defaultDate;
	}

	/**
	 * 获取一周的开始时间和结束时间
	 * 
	 * @param date
	 *            指定的时间，默认是今天
	 * @return key: StartDate EndDate
	 */
	public static Map<String, Date> GetWeekStartAndEndDate(Date date) {
		Map<String, Date> map = new HashMap<String, Date>();
		Date nowDate = new Date();
		if (date != null) {
			nowDate = date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nowDate);

		Integer dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			dayOfWeek += 7;
		}
		calendar.add(Calendar.DATE, 2 - dayOfWeek);
		Date startDate = calendar.getTime();
		map.put("StartDate", startDate);

		calendar.add(Calendar.DATE, 6);
		Date endDate = calendar.getTime();
		map.put("EndDate", endDate);

		return map;
	}

	/**
	 * 获取当前日期是星期几
	 * 
	 * @param dt
	 *            日期
	 * @return 当前日期是星期几
	 */
	public static String GetWeekOfDate(Date dt) {
		if (dt == null) {
			dt = new Date();
		}
		//String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		String[] weekDays = { "周日","周一", "周二", "周三", "周四", "周五", "周六" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	/**
	 * 获取指定日期一周数据
	 * 
	 * @param date
	 * @return Map<星期几,日期>
	 */
	public static Map<String, Date> GetWeekDays(Date date) {
		Map<String, Date> daysMap = new LinkedHashMap<String, Date>();
		try {
			Map<String, Date> map = GetWeekStartAndEndDate(date);

			Date startDate = map.get("StartDate");

			for (int i = 0; i < 7; i++) {
				Date dt = Add(startDate, Calendar.DATE, i);
				String dayofweek = GetWeekOfDate(dt);
				daysMap.put(dayofweek, dt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return daysMap;
	}

	/**
	 * 获取指定日期一周数据
	 * 
	 * @param date
	 * @return List<Map.Keys<week,date>>
	 */
	public static List<Map<String,Object>> GetWeekDayList(Date date) {
		List<Map<String,Object>> days= new ArrayList<Map<String,Object>>();
		try {
			Map<String, Date> map = GetWeekStartAndEndDate(date);

			Date startDate = map.get("StartDate");
			
			for (int i = 0; i < 7; i++) {
				Date dt = Add(startDate, Calendar.DATE, i);
				String dayofweek = GetWeekOfDate(dt);
				days.add(new HashMap<String, Object>(){
					{
						put("week", dayofweek);
						put("date",ToDateString(dt,DEFAULT_DATE));
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return days;
	}
	/**
	 * 获取当前时间往前的7天的时间(不包括今天)
	 * @param date
	 * @return
	 */
	public static List<Map<String,Object>> GetWeekDayList2(Date date) {
		List<Map<String,Object>> days= new ArrayList<Map<String,Object>>();
		for (int i = 0; i < 8; i++) {
			Date dt = Add(date, Calendar.DATE, -(i+1));
			String dayofweek = GetWeekOfDate(dt);
			days.add(new HashMap<String, Object>(){
				{
					put("week", dayofweek);
					put("date",ToDateString(dt,DEFAULT_DATE));
				}
			});
		}
		return days;
	}
	
	/**
	 * 指定时间添加指定的时间
	 * 
	 * @param dt
	 *            时间
	 * @param dateType
	 *            添加的时间类型 Calendar.DATE |Calendar.MONTH...
	 * @param dates
	 *            正--添加 ，负--减少
	 * @return
	 */
	public static Date Add(Date dt, int dateType, int dates) {
		if (dt == null) {
			dt = new Date();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		int date = calendar.get(dateType);
		calendar.set(dateType, date + dates);

		return calendar.getTime();
	}
	
	/**  
     * 计算两个日期之间相差的天数  
     * @param minDate 较小的时间 
     * @param maxDate  较大的时间 
     * @return 相差天数 
     */    
    public static int DaysBetween(Date minDate,Date maxDate)   {    

        minDate=ParseDate(ToDateString(minDate,DEFAULT_DATE),DEFAULT_DATE);
        maxDate=ParseDate(ToDateString(maxDate,DEFAULT_DATE),DEFAULT_DATE);
        
        Calendar cal = Calendar.getInstance();    
        cal.setTime(minDate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(maxDate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return DataConvert.ToInteger(between_days);           
    } 
}
