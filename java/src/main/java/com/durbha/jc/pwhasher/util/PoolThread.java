package com.durbha.jc.pwhasher.util;

import java.util.concurrent.BlockingQueue;

import java.util.concurrent.TimeUnit;

/**
 * A simple thread implementation. This will be what goes into a thread poo.
 * 
 * <p>Its job is to poll on the {@link #taskQueue} until a new request is available. See {@link ThreadPool} for details on how the {@link #taskQueue} is populated.
 * 
 * <p>It also waits a maximum of {@link #POLL_WAIT_MAX_SECONDS} second on the polling. When it comes out of the polling after {@link #POLL_WAIT_MAX_SECONDS} seconds, it will check for the {@link #isStopped} flag
 * If it is set, then it will terminate itself. If not, then it will go into polling again. 
 * 
 * @author seetharama
 *
 */
public class PoolThread extends Thread {
    private BlockingQueue<Runnable> taskQueue = null;
    private boolean       isStopped = false;
    private static final int POLL_WAIT_MAX_SECONDS = 10;

    public PoolThread(BlockingQueue<Runnable> queue){
        taskQueue = queue;
    }

    public void run(){
        while(!isStopped){
            try{
                Runnable runnable = (Runnable) taskQueue.poll(POLL_WAIT_MAX_SECONDS, TimeUnit.SECONDS); //So that we can come out in 10 seconds and check isStopped
                if (isStopped) {
                	break;
                }
                if (runnable != null) {
                    runnable.run();
                }
            } catch(InterruptedException e){
                //log or otherwise report exception,
                //but keep pool thread alive.
            }
        }
    }

    /**
     * Call this method to stop this thread.
     */
    public void doStop(){
        isStopped = true;
        this.interrupt(); //break pool thread out of poll() call.
    }
}
