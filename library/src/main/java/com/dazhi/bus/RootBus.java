package com.dazhi.bus;

import androidx.annotation.NonNull;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能：轻量的事件总线
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2018/4/19 14:55
 * 修改日期：2018/4/19 14:55
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class RootBus {
    final Map<String, ObservableImpl<Object>> mapBusEvent;

    private RootBus() {
        mapBusEvent = new ConcurrentHashMap<>();
    }

    private static final class classHolder {
        static final RootBus INSTANCE = new RootBus();
    }

    private static RootBus self() {
        return classHolder.INSTANCE;
    }

    public static Observable<Object> get(@NonNull String key) {
        return self().get(key, Object.class);
    }

    public static <T> Observable<T> get(@NonNull Class<T> eventType) {
        return self().get(eventType.getName(), eventType);
    }

    @SuppressWarnings({"unchecked"})
    private <T> Observable<T> get(@NonNull String key, @NonNull Class<T> type) {
        if (!mapBusEvent.containsKey(key)) {
            mapBusEvent.put(key, new ObservableImpl<>(key, new SoftReference<>(mapBusEvent)));
        }
        return (Observable<T>) mapBusEvent.get(key);
    }

}

