package com.durbha.jc.pwhasher.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class PoolThread extends Thread {
    private BlockingQueue<Runnable> taskQueue = null;
    private boolean       isStopped = false;

    public PoolThread(BlockingQueue<Runnable> queue){
        taskQueue = queue;
    }

    public void run(){
        while(!isStopped){
            try{
                Runnable runnable = (Runnable) taskQueue.poll(10, TimeUnit.SECONDS); //So that we can come out in 10 seconds and check isStopped
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

    public void doStop(){
        isStopped = true;
        this.interrupt(); //break pool thread out of poll() call.
    }
}
