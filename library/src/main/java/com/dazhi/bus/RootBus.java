package com.dazhi.bus;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import java.util.HashMap;
import java.util.Iterator;
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
    final Map<String, EventLiveData<Object>> mapBusEvent;

    private RootBus() {
        mapBusEvent = new ConcurrentHashMap<>();
    }

    private static final class classHolder {
        static final RootBus INSTANCE = new RootBus();
    }

    private static RootBus own() {
        return classHolder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private <T> EventLiveData<T> get(@NonNull String key) {
        return (EventLiveData<T>) mapBusEvent.get(key);
    }

    @SuppressWarnings("unchecked")
    private static <T> void send(T event, boolean isMain) {
        EventLiveData<T> temp = own().get(event.getClass().getName());
        if (temp != null) {
            if (RootBusExecutor.own().isMainThread()) {
                temp.setValue(new Event<>(event, isMain));
            } else {
                temp.postValue(new Event<>(event, isMain));
            }
        }
    }

    // 接收数据在IO线程，注意池中只开了4个核心线程，因此耗时任务不要死循环或死锁占用线程影响其它任务
    public static <T> void post(T event) {
        send(event, false);
    }

    // 接收数据在UI线程，因此接收器内不允许做耗时操作
    public static <T> void postToMain(T event) {
        send(event, true);
    }

    @SuppressWarnings("unchecked")
    @MainThread
    private <T> void _register(@NonNull Class<T> eventType, @NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        final String key = eventType.getName();
        EventLiveData<?> existing = mapBusEvent.get(key);
        if (existing == null) {
            EventLiveData<T> newLd = new EventLiveData<>(key, new EventLiveData.Callback() {
                @Override
                public void remove() {
                    mapBusEvent.remove(key);
                }
            });
            newLd.observe(owner, observer);
            mapBusEvent.put(key, (EventLiveData<Object>) newLd);
        }else {
            existing.observe(owner, observer);
        }
    }
    public static <T> void register(@NonNull Class<T> eventType, @NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        own()._register(eventType, owner, observer);
    }

    @SuppressWarnings("unchecked")
    @MainThread
    private <T> void _registerForever(@NonNull Class<T> eventType, @NonNull Observer<T> observer) {
        final String key = eventType.getName();
        EventLiveData<?> existing = mapBusEvent.get(key);
        if (existing == null) {
            EventLiveData<T> newLd = new EventLiveData<>(key, new EventLiveData.Callback() {
                @Override
                public void remove() {
                    mapBusEvent.remove(key);
                }
            });
            newLd.observeForever(observer);
            mapBusEvent.put(key, (EventLiveData<Object>) newLd);
        }else {
            existing.observeForever(observer);
        }
    }
    public static <T> void registerForever(@NonNull Class<T> eventType, @NonNull Observer<T> observer) {
        own()._registerForever(eventType, observer);
    }

    @MainThread
    private <T> void _unregister(@NonNull Class<T> eventType, @NonNull Observer<T> observer) {
        String key = eventType.getName();
        EventLiveData<?> existing = mapBusEvent.get(key);
        if (existing != null) {
            existing.removeObserver(observer);
        }
    }
    public static <T> void unregister(@NonNull Class<T> eventType, @NonNull Observer<T> observer) {
        own()._unregister(eventType, observer);
    }

    /**
     * 作者：WangZezhi  (20-12-9  下午5:23)
     * 功能：
     * 描述：
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class EventLiveData<T> extends MutableLiveData {
        interface Callback { void remove(); }
        final String key;
        final Callback mCallback;
        final HashMap<Observer, EventObserver<T>> mMapEvent = new HashMap<>();

        EventLiveData(@NonNull String key, @NonNull Callback mCallback) {
            this.key = key;
            this.mCallback = mCallback;
        }

        // 说明：此方法并发时,采取的是丢弃之前要最后一个值的策略，
        //      而我们不希望丢弃任何值，因此，切到主线程调用不丢的方法
        @Override
        public void postValue(final Object value) {
            RootBusExecutor.own().executeOnMain(new Runnable() {
                @Override
                public void run() {
                    setValue(value);
                }
            });
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer observer) {
            // 同一个观察者只能放入一次，这里抛出异常帮助查找问题
            if (mMapEvent.containsKey(observer)) {
                throw new IllegalArgumentException("Cannot add the same observer");
            }
            EventObserver<T> e = new EventObserver(observer);
            mMapEvent.put(observer, e);
            super.observe(owner, e);
        }

        @Override
        public void observeForever(@NonNull Observer observer) {
            if (mMapEvent.containsKey(observer)) {
                throw new IllegalArgumentException("Cannot add the same observer");
            }
            EventObserver<T> e = new EventObserver(observer);
            mMapEvent.put(observer, e);
            super.observeForever(e);
        }

        @Override
        public void removeObserver(@NonNull Observer observer) {
            if (!mMapEvent.containsKey(observer)) {
                return;
            }
            EventObserver<T> temp = mMapEvent.get(observer);
            mMapEvent.remove(observer);
            super.removeObserver(temp);
        }

        @Override
        protected void onInactive() {
            if(mMapEvent.size()>0) {
                Iterator<Observer> mIterator = mMapEvent.keySet().iterator();
                Observer temp;
                while (mIterator.hasNext()) {
                    temp = mIterator.next();
                    removeObserver(temp);
                }
                mMapEvent.clear();
            }
            mCallback.remove();
         }
    }

    private static final class EventObserver<T> implements Observer<Event<T>> {
        final Observer<? super T> mObserver;

        EventObserver(final Observer<? super T> observer) {
            mObserver = observer;
        }

        @Override
        public void onChanged(@Nullable final Event<T> t) {
            if (t.isMain) {
                mObserver.onChanged(t.event);
            } else {
                RootBusExecutor.own().executeOnIo(new Runnable() {
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

