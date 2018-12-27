package cn.core.timerJob;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerJobTest {

	
	private static final Logger log = LoggerFactory.getLogger(TimerJobTest.class);

	
	public static class Task1 extends AbstractTimerTask{

		int orderId,productId,userId;
		
		public Task1(int orderId,int productId,int userId) {
			this.orderId=orderId;
			this.productId=productId;
			this.userId=userId;
		}
		
		@Override
		public void run() {
			//TODO 根据参数进行操作
			
			log.info(id()+" 执行   "+ new Date().toLocaleString());
			
			System.out.println(id()+" 执行   "+ new Date().toLocaleString()+" -->"+Thread.currentThread().getName());  
		}
		@Override
		public String id() {
			return orderId+"_"+productId+"_"+userId;
		}
		@Override
		public String name() {
			return "Task_1";
		}
	}
	
	public static class Task2 extends AbstractTimerTask{

		@Override
		public void run() {
			System.out.println(id()+" 执行  "+new Date().toLocaleString()+" -->"+Thread.currentThread().getName());  
		}
		@Override
		public String id() {
			return "Task_2";
		}
		@Override
		public String name() {
			return "Task_2";
		}
	}
	
	
	public static void main(String[] args) {
	
		try {
			
			System.out.println("main start"+new Date().toLocaleString());
			
			TimerJobManager.getInstance().join(new Task1(1,1,1)).start(1,TimeUnit.SECONDS);
			
			TimerJobManager.getInstance().join(new Task2()).start(6,TimeUnit.SECONDS);
			
			TimerJobManager.getInstance().join(new Task2(),true).start(3,TimeUnit.SECONDS);
			
			TimerJobManager.getInstance().join(new Task1(1,2,2),true).start(2,TimeUnit.SECONDS);
			
			TimerJobManager.getInstance().join(new Task1(1,2,3),true).start(6,TimeUnit.SECONDS);
			
			Thread thread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							TimerJobManager.getInstance().cancel(new Task2());

							System.out.println("Task_2 cancel "+new Date().toLocaleString()+" -->"+Thread.currentThread().getName());  
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		
		Thread.sleep(4000);
		
		thread.start();
		
		System.out.println("main  end"+new Date().toLocaleString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
