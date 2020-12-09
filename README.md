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
