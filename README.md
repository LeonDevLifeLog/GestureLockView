
# GestureLockView 九宫格手势解锁控件

[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
![Build](https://github.com/LeonDevLifeLog/GestureLockView/workflows/Build/badge.svg)
[![](https://jitpack.io/v/LeonDevLifeLog/GestureLockView.svg)](https://jitpack.io/#LeonDevLifeLog/GestureLockView)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/68b4a9ad824148ada39b0dfbecfc6ffb)](https://www.codacy.com/manual/LeonDevLifeLog/GestureLockView?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=LeonDevLifeLog/GestureLockView&amp;utm_campaign=Badge_Grade)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![GitHub license](https://img.shields.io/github/license/LeonDevLifeLog/GestureLockView)](https://github.com/LeonDevLifeLog/GestureLockView/blob/master/LICENSE)

## Preview    预览

|this projcet|MIUI OS|
|--|--|
| ![本项目实现](/art/my.gif "My") | ![miui实现](/art/miui.gif "MIUI")   |

## Notice 说明


* 每个点代表一个字母,从a到z(需求超过5行5列(25)的暂时支持)

* Every point is a char from a to z(so more than 5 col and 5 row is not support now)

### How to use 引入库依赖

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}


dependencies {
        implementation 'com.github.LeonDevLifeLog:GestureLockView:v0.0.1' //请手动替换最新版本
}

```
## Layout sample 自定义属性
```xml
<com.github.leondevlifelog.gesturelockview.GestureLockView
        android:id="@+id/customGestureLockView"
        android:layout_width="229dp"
        android:layout_height="241dp"
        android:layout_marginEnd="8dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="76dp"
        android:padding="0dp"
        app:col="4"   <!--点的列数 默认3列-->
        app:dot_color="#121212" <!--点的颜色 默认-->
        app:dot_color_pressed="#FF2254A6"  <!--点被按着的时候的颜色-->
        app:dot_pressed_radius="6dp"  <!--点被点击的时候显示的区域大小-->
        app:dot_radius="6dp" <!--点的半径-->
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:line_color="#55CCCCCC"    <!--点与点之间线条轨迹的颜色-->
        app:line_width="12dp"   <!--直线轨迹线宽-->
        app:min_length="4"    <!--轨迹/密码最小长度-->
        app:row="4"  <!--点的行数 默认3行-->
        app:security_mode="false"    <!--是否启用安全模式(不带轨迹)-->
        app:vibrate="true"    <!--震动开关--> />
```

##  SetListener 使用方法  

使用时只要给控件setOnCheckPasswordListener,实现以下接口  
```java
public interface OnCheckPasswordListener {
        /**
         * 手势密码输入完成时回调,验证密码
         * <br>这里只做密码校验
         *
         * @param passwd 输入完成的手势密码
         * @return <code>true</code>:输入的手势密码和存储在本地的密码一致
         * <br>反之<code>false</code>
         */
        boolean onCheckPassword(String passwd);

        /**
         * 当密码校验成功时的回调
         */
        void onSuccess();

        /**
         * 当密码校验失败时的回调
         */
        void onError();
    }
```

## Plan

- [ ] 自定义密码错误或长度不够的时候的主颜色  
- [ ] 点可否被重复连接(增加密码强度)  
- [ ] 错误次数限制  
- [ ] 行列都超过5的情况下优化一下

## Licenses

```
Copyright 2020 Leon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
