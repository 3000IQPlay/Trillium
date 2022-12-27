package dev._3000IQPlay.trillium.util.phobos;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@SuppressWarnings("UnusedReturnValue")
public class ThreadManager implements GlobalExecutor
{

    public Future<?> submit(SafeRunnable runnable)
    {
        return submitRunnable(runnable);
    }

    public Future<?> submitRunnable(Runnable runnable)
    {
        return EXECUTOR.submit(runnable);
    }

    /**
     * Shuts down {@link GlobalExecutor#EXECUTOR}.
     */
    public void shutDown()
    {
        EXECUTOR.shutdown();
    }

}