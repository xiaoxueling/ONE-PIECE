package cn.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 缓存
 *@author xiaoxueling
 *@time 2018年2月12日
 *@version 1.0
 *<pre>
 *	CacheManager<K,V> cache=new CacheManager<K,V>();
 *</pre>
 */
@Service
public class CacheManager<K,V> {

	private int maximumSize;//最大缓存数目
	private int expireAfterWriteDuration;//数据存在有效期
	private TimeUnit timeUnit=TimeUnit.MINUTES;//数据有效期单位
	private LoadingCache<K, V> cache;
	
	/**
	 * 获取缓存数据
	 * @param key 缓存的Key
	 * @param dataSupplier 要缓存的数据
	 * <pre>
	 *调用实例：
	 *  V getOrCreate(K,()->{
	 *  	数据获取
	 *  });
	 * </pre>
	 * @return
	 * @throws ExecutionException 
	 */
	public V getOrCreate(K key,Supplier<V> dataSupplier){
		V v=null;
		if(cache==null){
			InitCache(key,dataSupplier);
		}
		try{
			v=cache.getUnchecked(key);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return v;
	}
	
	/**
	 * 清除指定Key的缓存数据
	 * @param key
	 */
	public void clear(K key){
		cache.invalidate(key);
	}
	
	/**
	 * 清除所有的缓存数据
	 */
	public void clearAll(){
		cache.invalidateAll();
	}
	
	/**
	 * InitCache
	 * @param key
	 * @param dataSupplier
	 */
	private  void InitCache(K key,Supplier<V> dataSupplier){
		
		cache=getCacheBuilder().build(new CacheLoader<K, V>(){
			@Override
			public V load (K key) {
				return dataSupplier.get();
			}
		});
	}
	
	/**
	 * CacheBuilder
	 * @return
	 */
	private CacheBuilder<Object, Object> getCacheBuilder(){
		CacheBuilder<Object, Object> builder=CacheBuilder.newBuilder();
		
		if(maximumSize>0){
			builder.maximumSize(maximumSize);
		}
		if(expireAfterWriteDuration>0){
			builder.expireAfterWrite(expireAfterWriteDuration, timeUnit);
		}
		
		return builder;
	}

	public int getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(int maximumSize) {
		this.maximumSize = maximumSize;
	}

	public int getExpireAfterWriteDuration() {
		return expireAfterWriteDuration;
	}

	public void setExpireAfterWriteDuration(int expireAfterWriteDuration) {
		this.expireAfterWriteDuration = expireAfterWriteDuration;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
	
}
