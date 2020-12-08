package com.dazhi.bus;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

/**
 * 功能：被观察者定义
 * 描述：通过此被观察者的post方法发送事件，可在注册的观察者监听方法中接收事件
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 日期：20-12-8 下午3:44
 */
public interface Observable<T> {
    void post(T event);

    @MainThread
    void register(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer);
    @MainThread
    void registerForever(@NonNull Observer<T> observer);

    // 取消注册(粘性注册需手动调用此方法；非粘性注册无需调用，内部自动调用)
    @MainThread
    void unregister(@NonNull final Observer<? super T> observer);
    @MainThread
    void unregisters(@NonNull final LifecycleOwner owner);

}
