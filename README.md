# JAVA推荐系统

#### 系统原理
该系统使用java编写的基于用户的协同过滤算法（UserCF）
利用统计学的相关系数经常皮尔森（pearson）相关系数计算相关系数来实现千人千面的推荐系统。

协同过滤推荐算法是诞生最早，并且较为著名的推荐算法。主要的功能是预测和推荐。算法通过对用户历史行为数据的挖掘发现用户的偏好，基于不同的偏好对用户进行群组划分并推荐品味相似的商品。协同过滤推荐算法分为两类，分别是基于用户的协同过滤算法(user-based collaboratIve filtering)，和基于物品的协同过滤算法(item-based collaborative filtering)。简单的说就是：人以类聚，物以群分。

皮尔森(pearson)相关系数公式

![输入图片说明](https://images.gitee.com/uploads/images/2020/0731/193612_8dfc4af8_1981977.png "屏幕截图.png")

公式定义为： 两个连续变量(X,Y)的pearson相关性系数(Px,y)等于它们之间的协方差cov(X,Y)除以它们各自标准差的乘积(σX,σY)。系数的取值总是在-1.0到1.0之间，接近0的变量被成为无相关性，接近1或者-1被称为具有强相关性。
通常情况下通过以下取值范围判断变量的相关强度：
相关系数          0.8-1.0     极强相关
                 0.6-0.8     强相关
                 0.4-0.6     中等程度相关
                 0.2-0.4     弱相关
                 0.0-0.2     极弱相关或无相关

java代码实现

![输入图片说明](https://images.gitee.com/uploads/images/2020/0731/195616_1a98b43e_1981977.png "屏幕截图.png")

#### 软件架构
Spring boot单项目

#### 安装教程

1.  git下载源码
2.  maven构建依赖
3.  idea-java运行
#### 使用说明

1. 找到  src / main / java / com / tarzan / recommend / RecommendSystemApplication.java  右键java 运行
![输入图片说明](https://images.gitee.com/uploads/images/2021/0513/134117_2e95c3e7_1981977.png "屏幕截图.png")
2.传入不同的用户id，得到不同的推荐数据
![输入图片说明](https://images.gitee.com/uploads/images/2021/0513/134306_fc20dd60_1981977.png "屏幕截图.png")

#### 技术交流&问题反馈

      刚刚整理的代码还有很多不足之处，如有问题请联系我

      联系QQ:1334512682 
      微信号：vxhqqh



