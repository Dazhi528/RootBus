package com.dazhi.bus;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

/**
 * 功能：粘性注册事件返回值
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 日期：20-12-10 下午3:41
 */
@SuppressWarnings({"rawtypes", "unused", "RedundantSuppression"})
public class RootBusDisposable {
    final Class eventType;
    final Observer observer;

    public RootBusDisposable(@NonNull Class eventType, @NonNull Observer observer) {
        this.eventType = eventType;
        this.observer = observer;
    }

    public void dispose() {
        RootBus.unregister(eventType, observer);
    }
}
