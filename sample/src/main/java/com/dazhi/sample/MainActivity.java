package com.dazhi.sample;

import android.widget.TextView;
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
    private static final String KEY_EVENT_STR = "KeyEventStr";

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
        binding.btClear.setOnClickListener(v -> binding.tvShow.setText(""));
        binding.btSend.setOnClickListener(v -> {
            RootBus.get(KEY_EVENT_STR).post("我是值\n");
            RootBus.get(Apple.class).post(new Apple("红色\n"));
            RootBus.get(Apple.class).post(new Apple("绿色\n"));
        });
        // 接收器注册
        RootBus.get(KEY_EVENT_STR).register(this, s -> binding.tvShow.append((String)s));
        RootBus.get(Apple.class).register(this, apple -> binding.tvShow.append(apple.getColor()));
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

}
