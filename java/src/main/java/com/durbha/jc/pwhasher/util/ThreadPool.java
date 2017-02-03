package com.durbha.jc.pwhasher.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import com.durbha.jc.pwhasher.Constants;

public class ThreadPool {
	private BlockingQueue<Runnable> taskQueue = null;
	private List<PoolThread> threads = new ArrayList<PoolThread>();
	private boolean isStopped = false;
	private Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

	public ThreadPool(int noOfThreads, int maxCapacity) {
		taskQueue = new ArrayBlockingQueue<Runnable>(maxCapacity);

		for (int i = 0; i < noOfThreads; i++) {
			threads.add(new PoolThread(taskQueue));
		}
		for (PoolThread thread : threads) {
			thread.start();
		}
	}

	public void execute(Runnable task) throws IllegalStateException {
		if (this.isStopped) {
			throw new IllegalStateException("ThreadPool is stopped");
		}

		try {
			this.taskQueue.put(task);
		} catch (InterruptedException e) {
			logger.warning("Could not add task to queue: " + e.getMessage());
			throw new IllegalStateException("Could not add task to queue");
		}
	}

	public void stop() {
		this.isStopped = true;
		for (PoolThread thread : threads) {
			thread.doStop();
		}
		int index=1;
		for (PoolThread thread : threads) {
			try {
				logger.info("Waiting for thread " + index++ + " to stop...");
				thread.join(); //Wait until all threads are finished
			} catch (InterruptedException e) {
			}
		}
	}

}
