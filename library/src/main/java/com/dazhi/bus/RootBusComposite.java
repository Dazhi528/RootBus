package com.dazhi.bus;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 功能：事件收集器
 * 描述：用于统一销毁
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 日期：20-12-10 下午2:33
 */
public class RootBusComposite {
    private final List<RootBusDisposable> lsRootBusDisposable = new ArrayList<>();

    @SuppressWarnings("UnusedReturnValue")
    public synchronized RootBusComposite add(@NonNull RootBusDisposable mRootBusDisposable) {
        lsRootBusDisposable.add(mRootBusDisposable);
        return this;
    }

    public synchronized void dispose() {
        if(lsRootBusDisposable.size()==0) {
            return;
        }
        Iterator<RootBusDisposable> mIterator = lsRootBusDisposable.iterator();
        RootBusDisposable temp;
        while (mIterator.hasNext()) {
            temp = mIterator.next();
            RootBus.unregister(temp.eventType, temp.observer);
            mIterator.remove();
        }
    }

}
