
# GestureLockView

[ ![Download](https://api.bintray.com/packages/leondevlifelog/maven/gesturelockview/images/download.svg) ](https://bintray.com/leondevlifelog/maven/gesturelockview/_latestVersion)

九宫格手势解锁控件

## 实例演示
|本项目实现|MIUI实现|
|--|--|
| ![本项目实现](/art/my.gif "My") | ![miui实现](/art/miui.gif "MIUI")   |

## 说明

1. z型路径,每个点代表一个字母,从a到z(26),所以,需求超过5行5列(25)的暂时不要用这个控件,下一版我再限制一下或适配一下,行列较多(变态)需求的等等吧

### 引入库依赖

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}


dependencies {
        implementation 'com.github.LeonDevLifeLog:GestureLockView:v0.0.1'
}

```
## 自定义属性
```xml
<resources>
    <declare-styleable name="GestureLockView">
        <!--点的行数 默认3行-->
        <attr name="row" format="integer" />
        <!--点的列数 默认3列-->
        <attr name="col" format="integer" />
        <!--点的颜色 默认-->
        <attr name="dot_color" format="color|reference" />
        <!--点的半径-->
        <attr name="dot_radius" format="dimension|reference" />
        <!--点被按着的时候的颜色-->
        <attr name="dot_color_pressed" format="color|reference" />

        <!--点与点之间线条轨迹的颜色-->
        <attr name="line_color" format="color|reference" />
        <!--直线轨迹线宽-->
        <attr name="line_width" format="dimension|reference" />
        <!--点被点击的时候显示的区域大小-->
        <attr name="dot_pressed_radius" format="dimension|reference" />
        <!--是否启用安全模式(不带轨迹)-->
        <attr name="security_mode" format="boolean" />
        <!--震动开关-->
        <attr name="vibrate" format="boolean" />
        <!--轨迹/密码最小长度-->
        <attr name="min_length" format="integer" />

    </declare-styleable>
</resources>
```

## 使用方法  

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
Copyright 2017 Leon

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
