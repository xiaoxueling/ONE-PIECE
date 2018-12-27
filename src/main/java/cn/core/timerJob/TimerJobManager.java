package cn.core.timerJob;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 管理
 * @author xiaoxueling
 *
 */
public class TimerJobManager {
	
	//线程池
	private static ScheduledExecutorService executorService;
	//实例
	private static TimerJobManager TIMER_JOB;
	//TimerJob
	private TimerJob<? extends AbstractTimerTask> timer_Job;
	
	public TimerJobManager() {
		executorService=Executors.newScheduledThreadPool(10);
	}
	
	public static TimerJobManager getInstance() {
		if(TIMER_JOB==null) {
			TIMER_JOB=new TimerJobManager();
		}
		return TIMER_JOB;
	}
	
	/**
	 * 设置要执行的task
	 * @param task task任务
	 * @return
	 */
	public  <T extends AbstractTimerTask> TimerJobManager  join(T task) {
		return this.join(task,false);
	}
	
	/**
	 * 设置要执行的task
	 * @param task task任务
	 * @param multiple 是否允许重复
	 * @return
	 */
	public  <T extends AbstractTimerTask> TimerJobManager  join(T task,boolean multiple) {
		if(task!=null) {
			timer_Job=new TimerJob<T>(task);

			if(!multiple) {
				timer_Job.cancelAndClearMuiltTask();
			}
			
			timer_Job.addCache();
		}
		return this;
	}
	
	/**
	 * 设置要取消的task
	 * @param task task任务
	 * @return
	 * @throws Exception 
	 */
	public  <T extends AbstractTimerTask> void  cancel(T task) throws Exception {
		if(task!=null) {
			new TimerJob<T>(task).cancelAndClearMuiltTask();
		}
	}
	
	/**
	 * 执行
	 * @param delay 延时多久执行
	 * @param unit TimeUnit
	 * @return
	 * @throws Exception 
	 */
	public void start(long delay,TimeUnit unit) throws Exception {
		if(timer_Job==null) {
			throw new Exception("timerJob is not by null！");
		}
		executorService.schedule(timer_Job, delay, unit);
	}
	
}
