package cn.core.timerJob;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.util.StringHelper;

/**
 * TimerJob
 * @author xiaoxueling
 *
 * @param <T>  AbstractTimerTask
 */
public class TimerJob<T extends AbstractTimerTask> implements Runnable { 
	
	//任务Id缓存 <JobId,TaskId>
	private static volatile ConcurrentHashMap<String, String>CACHE_JOBID=new ConcurrentHashMap<String, String>();
	//任务缓存 <JobId,Job>
	private static volatile ConcurrentHashMap<String, TimerJob<?>>CACHE_JOB=new ConcurrentHashMap<String, TimerJob<?>>();
	//是否标记为取消状态
	private volatile AtomicBoolean canceled=new AtomicBoolean(false);

	private String jobId=getUUID();
	
	//要执行的任务
	private T task;

	public TimerJob(T task) {
		this.task=task;
	}
	
	@Override
	public void run() {
		try {
			if(!canceled.get()) {
			
				 this.task.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				this.clear();
				this.canceled();
			} catch (Exception e2) {
				
			}
		}
	}
	
	/**
	 * 添加缓存
	 */
	public void addCache() {
		CACHE_JOBID.put(this.jobId,this.taskId());
		CACHE_JOB.put(this.jobId,this);
	}
	
	/**
	 * 取消并清理指定Task的任务
	 * @param taskId
	 */
	public  void cancelAndClearMuiltTask() {
		
		String taskId=this.taskId();
		
		if(CACHE_JOBID.containsValue(taskId)) {
			
			for (Map.Entry<String, String> item : CACHE_JOBID.entrySet()) {
				
				if(!item.getValue().equals(taskId)) {
					 continue;
				}
				
				String oldJobId=item.getKey();
				
				if(StringHelper.IsNullOrEmpty(oldJobId)) {
					continue;
				}
				CACHE_JOBID.remove(oldJobId);
				
				TimerJob<?> oldTimerJob=CACHE_JOB.get(oldJobId);
				if(oldTimerJob==null) {
					continue;
				}
				oldTimerJob.canceled();
				
				CACHE_JOB.remove(oldJobId);
				
			}
		}
	}
	
	/**
	 * 获取taskId
	 * @return
	 */
	private String taskId() {
		String taskId=task.id();
		if(StringHelper.IsNullOrEmpty(taskId)){
			taskId=getUUID();
		}
		return taskId;
	}
	
	/**
	 * 清除当前缓存
	 */
	private void clear() {
		CACHE_JOBID.remove(this.jobId);
		CACHE_JOB.remove(this.jobId);
	}
	
	/**
	 * 取消当前任务
	 */
	private void canceled() {
		canceled.set(true);
	}
	
	private String getUUID() {
		return UUID.randomUUID().toString();
	}
}
