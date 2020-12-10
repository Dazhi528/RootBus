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

### 用法（实例详情见sample模块）
```
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
// 粘性注册方式
Observer<Apple> mAppleObserver = apple -> binding.tvShow.append(apple.getColor());
RootBus.registerForever(Apple.class, mAppleObserver);

// 通用注销方式：粘性注册需手动注销，否则会内存泄露
RootBus.unregister(Apple.class, mAppleObserver);
或
new RootBusComposite().add(xxx).dispose();

// 非粘性注册方式； 说明：非粘性注册无需注销，跟随生命周期自动注销(当然完全可以调用如上的手动注销方法)
RootBus.register(Apple.class, this, mAppleObserver);

3）发送
// 值在工作线程回调接收
RootBus.post(new Apple("红色\n"));
// 值在UI线程回调接收
RootBus.postToMain(new Apple("绿色\n"));
```