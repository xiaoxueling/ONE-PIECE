package cn.core.timerJob;

/**
 * 执行任务抽象方法
 * @author xiaoxueling
 *
 */

public abstract class AbstractTimerTask{

	/**
	 * 任务Id 
	 * @return
	 */
	public abstract String id();
	
	/**
	 * 任务名称
	 * @return
	 */
	public String name() {
		return "";
	};
	
	/**
	 * 执行的任务
	 */
	public abstract void run();
}
