package com.durbha.jc.pwhasher.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import com.durbha.jc.pwhasher.Constants;

/**
 * A Thread pool implementation. It essentially provides two things.
 * 
 * <ol>
 *    <li>Create threads based on the number of threads parameter</li>
 *    <li>Add an incoming request to a BlockingQueue, which is then polled by the individual threads, created above</li>
 * </ol>
 * 
 * @author seetharama
 *
 */
public class ThreadPool {
	private BlockingQueue<Runnable> taskQueue = null;
	private List<PoolThread> threads = new ArrayList<PoolThread>();
	private boolean isStopped = false;
	private Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

	/**
	 * Create the given number of threads, and a BlockingQueue based on the maxCapacity
	 * 
	 * @param noOfThreads Number of threads to create
	 * @param maxCapacity Maximum capacity of the Queue, after this, any request to {@link #execute(Runnable)} will throw a IllegalStateException.
	 */
	public ThreadPool(int noOfThreads, int maxCapacity) {
		taskQueue = new ArrayBlockingQueue<Runnable>(maxCapacity);

		for (int i = 0; i < noOfThreads; i++) {
			threads.add(new PoolThread(taskQueue));
		}
		for (PoolThread thread : threads) {
			thread.start();
		}
	}

	/**
	 * Execute a given runnable. Essentially, this will be added to the Queue, which is polled by the individual threads.
	 * <p>If the queue reaches the limit, then an IllegalStateException is thrown.
	 * 
	 * @param task Runnable that needs to be executed by a thread
	 * @throws IllegalStateException If either the application is stopped, or the queue is full
	 */
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

	/**
	 * Call this to stop all threads
	 */
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
