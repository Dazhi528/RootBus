package com.dazhi.bus;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

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
    private final Map<String, ObservableImpl<Object>> mapBusEvent;  // 存放LiveEvent

    private RootBus() {
        mapBusEvent = new HashMap<>();
    }

    private static final class classHolder {
        static final RootBus INSTANCE = new RootBus();
    }

    public static RootBus self() {
        return classHolder.INSTANCE;
    }





    private Observable<Object> get(@NonNull String key) {
        return get(key, Object.class);
    }
    private <T extends Observable> Observable<T> get(@NonNull Class<T> eventType) {
        return get(eventType.getName(), eventType);
    }
    private synchronized <T> Observable<T> get(@NonNull String key, @NonNull Class<T> type) {
        if (!mapBusEvent.containsKey(key)) {
            mapBusEvent.put(key, new ObservableImpl<>());
        }
        return (Observable<T>) mapBusEvent.get(key);
    }



    public void post(Object event) {

    }

    @MainThread
    public void register(@NonNull LifecycleOwner owner, @NonNull Observer observer) {

    }

    @MainThread
    public void registerForever(@NonNull Observer observer) {

    }

    @MainThread
    public void unregister(@NonNull Observer observer) {

    }

    @MainThread
    public void unregisters(@NonNull LifecycleOwner owner) {

    }

}

