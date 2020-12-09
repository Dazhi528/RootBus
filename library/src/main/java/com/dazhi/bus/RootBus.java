package com.dazhi.bus;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
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
    final Map<String, KeyLiveData<Object>> mapBusEvent;

    private RootBus() {
        mapBusEvent = new ConcurrentHashMap<>();
    }

    private static final class classHolder {
        static final RootBus INSTANCE = new RootBus();
    }

    private static RootBus self() {
        return classHolder.INSTANCE;
    }

    private <T> KeyLiveData<T> get(@NonNull String key) {
        return (KeyLiveData<T>) mapBusEvent.get(key);
    }

    private static <T> void send(T event, boolean isMain) {
        Class cur = event.getClass();
        KeyLiveData<T> temp = self().get(cur.getName());
        if (temp != null) {
            if (RootBusExecutor.own().isMainThread()) {
                temp.setValue(new Event<>(event, isMain));
            } else {
                temp.postValue(new Event<>(event, isMain));
            }
        }
    }

    public static <T> void post(T event) {
        send(event, false);
    }

    public static <T> void postToMain(T event) {
        send(event, true);
    }

    @MainThread
    private <T> void register(@NonNull Class<T> eventType, @NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        String key = eventType.getName();
        KeyLiveData<?> existing = mapBusEvent.get(key);
        if (existing == null) {
            KeyLiveData<T> newLd = new KeyLiveData<>();
            newLd.observe(owner, new EventObserver(observer));
            mapBusEvent.put(key, (KeyLiveData<Object>) newLd);
        }
    }

    @MainThread
    private <T> void registerForever(@NonNull Class<T> eventType, @NonNull Observer<T> observer) {
        String key = eventType.getName();
        KeyLiveData<?> existing = mapBusEvent.get(key);
        if (existing == null) {
            KeyLiveData<T> newLd = new KeyLiveData<>();
            newLd.observeForever(new EventObserver(observer));
            mapBusEvent.put(key, (KeyLiveData<Object>) newLd);
        }
    }

    @MainThread
    private <T> void unregister(@NonNull Class<T> eventType, @NonNull Observer<T> observer) {
        String key = eventType.getName();
        KeyLiveData<?> existing = mapBusEvent.get(key);
        if (existing != null) {
            existing.removeObserver(observer);
        }
    }

    @MainThread
    private <T> void unregisters(@NonNull Class<T> eventType, @NonNull LifecycleOwner owner) {
        String key = eventType.getName();
        KeyLiveData<?> existing = mapBusEvent.get(key);
        if (existing != null) {
            existing.removeObservers(owner);
        }
    }

    /**
     * 作者：WangZezhi  (20-12-9  下午5:23)
     * 功能：
     * 描述：
     */
    private static final class KeyLiveData<T> extends MutableLiveData {
        final HashMap<Observer, EventObserver<T>> mMapEvent = new HashMap<>();

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer observer) {
            if (mMapEvent.containsKey(observer)) {
                return;
            }
            EventObserver<T> temp = new EventObserver(observer);
            mMapEvent.put(observer, temp);
            super.observe(owner, temp);
        }

        @Override
        public void observeForever(@NonNull Observer observer) {
            if (mMapEvent.containsKey(observer)) {
                return;
            }
            EventObserver<T> temp = new EventObserver(observer);
            mMapEvent.put(observer, temp);
            super.observeForever(observer);
        }

        @Override
        public void removeObserver(@NonNull Observer observer) {
            if (!mMapEvent.containsKey(observer)) {
                return;
            }
            super.removeObserver(mMapEvent.get(observer));
            mMapEvent.remove(observer);
        }
    }

    private static final class EventObserver<T> implements Observer<Event<T>> {
        final MutableLiveData<Event<T>> mLiveData;
        final Observer<? super T> mObserver;

        EventObserver(final Observer<? super T> observer) {
            mLiveData = new MutableLiveData<>();
            mObserver = observer;
        }

        @Override
        public void onChanged(@Nullable final Event<T> t) {
            if (t.isMain) {
                mObserver.onChanged(t.event);
            } else {
                RootBusExecutor.own().execute(new Runnable() {
                    @Override
                    public void run() {
                        mObserver.onChanged(t.event);
                    }
                });
            }
        }
    }

    private static final class Event<T> {
        final T event;
        final boolean isMain;

        public Event(T event, boolean isMain) {
            this.event = event;
            this.isMain = isMain;
        }
    }

}

