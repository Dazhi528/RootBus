package com.dazhi.sample;

import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.dazhi.bus.RootBus;
import com.dazhi.libroot.root.RootSimpActivity;
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
        binding.btSend.setOnClickListener(v -> {
            // 类方式无需带上Key
            RootBus.post(new Apple("红色\n"));
            RootBus.post(new Apple("绿色\n"));
        });
        // 类方式
        Observer<Apple> mAppleObserver = apple -> binding.tvShow.append(apple.getColor());
        RootBus.registerForever(Apple.class, mAppleObserver);
        // 注销测试
        binding.btClear.setOnClickListener(v -> {
            binding.tvShow.setText("Apple.class 接收器已注销\n");
            RootBus.unregister(Apple.class, mAppleObserver);
        });
    }

    private static final class Apple {
        private final String color;

        public Apple(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }
    private static final class Person {

    }

}
