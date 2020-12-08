package com.dazhi.bus;

import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.ref.SoftReference;
import java.util.Map;

/**
 * 功能：被观察者定义内部实现
 * 描述：外部只允许通过接口调用
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 日期：20-12-8 下午4:45
 */
@SuppressWarnings("unchecked")
final class ObservableImpl<T> implements Observable<T> {
    private final MeLiveData<T> liveData;

    ObservableImpl(@NonNull String key,
                   @NonNull SoftReference<Map<String, ObservableImpl<T>>> srMap) {
        this.liveData = new MeLiveData<>(key, srMap);
    }

    @Override
    public void post(T event) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            liveData.setValue(event);
        } else {
            liveData.postValue(event);
        }
    }

    @MainThread
    @Override
    public void register(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        liveData.observe(owner, observer);
    }

    @MainThread
    @Override
    public void registerForever(@NonNull Observer<T> observer) {
        liveData.observeForever(observer);
    }

    @MainThread
    @Override
    public void unregister(@NonNull Observer<T> observer) {
        liveData.removeObserver(observer);
    }

    @MainThread
    @Override
    public void unregisters(@NonNull LifecycleOwner owner) {
        liveData.removeObservers(owner);
    }


    @SuppressWarnings("rawtypes")
    private static class MeLiveData<T> extends MutableLiveData {
        @NonNull
        private final String key;
        private final SoftReference<Map<String, ObservableImpl<T>>> srMap;

        MeLiveData(@NonNull String key,
                   @NonNull SoftReference<Map<String, ObservableImpl<T>>> srMap) {
            this.key = key;
            this.srMap = srMap;
        }

        @Override
        protected void onInactive() {
            Map<String, ObservableImpl<T>> temp = srMap.get();
            if(temp!=null) {
                temp.remove(key);
            }
        }
    }
}
