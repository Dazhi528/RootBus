package com.dazhi.bus;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
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
    private final List<EventComposite> lsEventComposite = new ArrayList<>();

    @SuppressWarnings("rawtypes")
    public static final class EventComposite {
        private final Class eventType;
        private final Observer observer;

        public EventComposite(@NonNull Class eventType, @NonNull Observer observer) {
            this.eventType = eventType;
            this.observer = observer;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized RootBusComposite add(@NonNull EventComposite mEventComposite) {
        lsEventComposite.add(mEventComposite);
        return this;
    }

    public synchronized void dispose() {
        if(lsEventComposite.size()==0) {
            return;
        }
        Iterator<EventComposite> mIterator = lsEventComposite.iterator();
        EventComposite temp;
        while (mIterator.hasNext()) {
            temp = mIterator.next();
            RootBus.unregister(temp.eventType, temp.observer);
            mIterator.remove();
        }
    }

}
