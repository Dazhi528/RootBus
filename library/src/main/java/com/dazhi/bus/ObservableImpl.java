package com.dazhi.bus;

import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * 功能：被观察者定义内部实现
 * 描述：外部只允许通过接口调用
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 日期：20-12-8 下午4:45
 */
final class ObservableImpl<T> implements Observable<T> {
    private final MutableLiveData<T> liveData;

    ObservableImpl() {
        this.liveData = new MutableLiveData<>();
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
    public void register(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        liveData.observe(owner, observer);
    }

    @MainThread
    @Override
    public void registerForever(@NonNull Observer<T> observer) {
        liveData.observeForever(observer);
    }

    @MainThread
    @Override
    public void unregister(@NonNull Observer<? super T> observer) {
        liveData.removeObserver(observer);
    }

    @MainThread
    @Override
    public void unregisters(@NonNull LifecycleOwner owner) {
        liveData.removeObservers(owner);
    }
}
