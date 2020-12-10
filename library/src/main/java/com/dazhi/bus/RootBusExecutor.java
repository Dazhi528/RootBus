package com.dazhi.bus;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能：事件Bus执行器
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 日期：20-12-9 下午4:14
 */
public class RootBusExecutor {
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService mEsIO = Executors.newFixedThreadPool(4, new ThreadFactory() {
        private static final String THREAD_NAME_FORMAT = "io_thread_%d";
        private final AtomicInteger mThreadId = new AtomicInteger(0);
        @SuppressLint("DefaultLocale")
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(String.format(THREAD_NAME_FORMAT, mThreadId.getAndIncrement()));
            return t;
        }
    });

    private RootBusExecutor(){}
    private static final class ClassHolder {
        static final RootBusExecutor INSTANCE = new RootBusExecutor();
    }
    public static RootBusExecutor own() {
        return ClassHolder.INSTANCE;
    }

    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public void executeOnIo(Runnable command) {
        mEsIO.execute(command);
    }
    public void executeOnMain(Runnable command) {
        mMainHandler.post(command);
    }
}
