package com.dazhi.bus;

import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：轻量的事件总线
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2018/4/19 14:55
 * 修改日期：2018/4/19 14:55
 */
public class RootBus {
    private final Map<String, ObservableImpl<Object>> mapBusEvent;

    private RootBus() {
        mapBusEvent = new HashMap<>();
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

    public static <T extends Observable> Observable<T> get(@NonNull Class<T> eventType) {
        return self().get(eventType.getName(), eventType);
    }

    private synchronized <T> Observable<T> get(@NonNull String key, @NonNull Class<T> type) {
        if (!mapBusEvent.containsKey(key)) {
            mapBusEvent.put(key, new ObservableImpl<>());
        }
        return (Observable<T>) mapBusEvent.get(key);
    }

}

