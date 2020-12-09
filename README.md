# RootBus
#### 说明：
LiveData 简单实现eventbus功能，用于替换RxBus,EventBus的一些简单使用场景
复杂场景时，如粘性事件等，请使用其它库(为了保持此库的轻量性，仅几行代码)

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
