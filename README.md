# RootBus
#### 说明：
LiveData实现的eventbus功能，用于替换RxBus,EventBus的一些使用场景(支持粘性事件) <br/>
此库由于是通过官方LiveData方式实现，因此仅仅几行代码，非常轻量，强烈推荐

### 引入方式
[![Release](https://img.shields.io/github/release/Dazhi528/RootBus?style=flat)](https://jitpack.io/#Dazhi528/RootBus)
[![API](https://img.shields.io/badge/API-19%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=19)

```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
    implementation 'com.github.Dazhi528:RootBus:x.x.x'
}
```

### 用法
```
// ============= 类方式
1) 定义Bean类
private static final class Apple {
    private final String color;
    public Apple(String color) {
       this.color = color;
    }
    public String getColor() {
       return color;
    }
}

2）注册接收器
Observer<Apple> mAppleObserver = apple -> binding.tvShow.append(apple.getColor());

// 非粘性注册无需注销，跟随生命周期自动注销
RootBus.get(Apple.class).register(mAppleObserver);

// 粘性注册需手动注销，否则会内存泄露
RootBus.get(Apple.class).registerForever(mAppleObserver);

// 粘性时，不用了需调用此方法手动注销
RootBus.get(Apple.class).unregister(mAppleObserver);

3）发送
RootBus.post(new Apple("红色\n"));
RootBus.post(new Apple("绿色\n"));

// ============= 字符串方式
1）定义成员变量Key
private static final String KEY_EVENT_STR = "KeyEventStr";

2）注册接收器(同样支持粘性注册方式，详情同如上的类方式)
RootBus.get(KEY_EVENT_STR).register(this, s -> binding.tvShow.append((String)s));

3）发送
RootBus.post(KEY_EVENT_STR, "我是值\n");
```