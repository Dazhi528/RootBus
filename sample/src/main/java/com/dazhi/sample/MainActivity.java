package com.dazhi.sample;

import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.dazhi.bus.RootBus;
import com.dazhi.bus.RootBusComposite;
import com.dazhi.bus.RootBusExecutor;
import com.dazhi.libroot.root.RootSimpActivity;
import com.dazhi.libroot.util.RtCmn;
import com.dazhi.sample.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

/**
 * 功能：用法实例
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 日期：20-9-9 下午6:36
 */
public class MainActivity extends RootSimpActivity<ActivityMainBinding> {
    @NotNull
    @Override
    protected ActivityMainBinding initBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initConfig(TextView tvToolTitle) {
        tvToolTitle.setText("事件总线");
    }

    @Override
    protected void initViewAndDataAndEvent() {
        // 用于收集粘性注册事件，可统一移除
        final RootBusComposite mRootBusComposite = new RootBusComposite();
        //
        binding.btSend.setOnClickListener(v -> {
            // UI线程发送到UI线程
            RootBus.postToMain(new Apple("红色"));

            // 工作线程发送,注意需要加点间隔，否则LiveData库会只接收最后一个，前面的丢弃
            new Thread(() -> {
                // 工作线程发送到UI线程
                RootBus.postToMain(new Person("李四", 15));
                // 工作线程发送到工作线程
                RootBus.post(new Person("张三", 13));
                //
                RootBus.postToMain(new Person("孙五", 18));
            }).start();
        });
        // 粘性注册方式，不用时，需要手动调用注销方法，否则会内存泄露
        Observer<Apple> mAppleObserver1 = apple -> binding.tvShow.append("观察者1；颜色："+apple.color+"\n");
        mRootBusComposite.add(RootBus.registerForever(Apple.class, mAppleObserver1));
        // 多次注册的情况
        Observer<Apple> mAppleObserver2 = apple -> binding.tvShow.append("观察者2；颜色："+apple.color+"\n");
        mRootBusComposite.add(RootBus.registerForever(Apple.class, mAppleObserver2));

        // 非粘性注册方式，无需手动调用注销
        RootBus.register( Person.class, this, mPerson -> {
            if(RootBusExecutor.own().isMainThread()) {
                binding.tvShow.append("当前线程："+Thread.currentThread().getName()+"；姓名："+mPerson.name+"；年龄："+mPerson.age+"\n");
            }else {
                RtCmn.toastShort("当前线程："+Thread.currentThread().getName()+"；姓名："+mPerson.name+"；年龄："+mPerson.age);
            }
        });
        // 注销测试
        binding.btClear.setOnClickListener(v -> {
            binding.tvShow.setText("Apple.class 接收器已注销\n");
            // RootBus.unregister(Apple.class, mAppleObserver1);
            // 测试：观察注销如下和不注销的区别
            // RootBus.unregister(Apple.class, mAppleObserver2);
            // 收集器统一移除方式
            mRootBusComposite.dispose();
        });
    }

    private static final class Apple {
        private final String color;

        public Apple(String color) {
            this.color = color;
        }
    }

    private static final class Person {
        private final String name;
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

}
