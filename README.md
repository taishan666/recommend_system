# JAVA推荐系统

#### 系统原理
该系统使用java编写的基于用户的协同过滤算法（UserCF）和基于物品（此应用中指电影）的协同过滤(ItemtemCF）
利用统计学的相关系数经常皮尔森（pearson）相关系数计算相关系数来实现千人千面的推荐系统。


#### 协同过滤算法
协同过滤推荐算法是诞生最早，并且较为著名的推荐算法。主要的功能是预测和推荐。协同过滤(Collaborative Filtering,简写CF)是推荐系统最重要得思想之一，其思想是根据用户之前得喜好以及其他兴趣相近得用户得选择来给用户推荐物品(基于对用户历史行为数据的挖掘发现用户的喜好偏向，并预测用户可能喜好的产品进行推荐)，一般仅仅基于用户的行为数据（评价，购买，下载等），而不依赖于物品的任何附加信息（物品自身特征）或者用户的任何附加信息（年龄，性别等）。其思想总的来说就是：人以类聚，物以群分。
目前应用比较广泛的协同过滤算法是基于邻域的方法，而这种方法主要有两种算法：
分别是:

- 基于用户的协同过滤算法(user-based collaboratIve filtering 简称 UserCF)
给用户推荐和他兴趣相似的其他用户喜欢的产品
- 基于物品的协同过滤算法(item-based collaborative filtering 简称 ItemCF)
给用户推荐和他之前喜欢的物品相似的物品

#### 皮尔森(pearson)相关系数公式

![输入图片说明](https://images.gitee.com/uploads/images/2020/0731/193612_8dfc4af8_1981977.png "屏幕截图.png")

公式定义为： 两个连续变量(X,Y)的pearson相关性系数(Px,y)等于它们之间的协方差cov(X,Y)除以它们各自标准差的乘积(σX,σY)。系数的取值总是在-1.0到1.0之间，接近0的变量被成为无相关性，接近1或者-1被称为具有强相关性。
 
 _皮尔森相关系数反映了两个变量的线性相关性的强弱程度，r的绝对值越大说明相关性越强。_ 


- 当r>0时，表明两个变量正相关，即一个变量值越大则另一个变量值也会越大；
- 当r<0时，表明两个变量负相关，即一个变量值越大则另一个变量值反而会越小；
- 当r=0时，表明两个变量不是线性相关的（注意只是非线性相关），但是可能存在其他方式的相关性（比如曲线方式）；
- 当r=1和-1时，意味着两个变量X和Y可以很好的由直线方程来描述，所有样本点都很好的落在一条直线上。


通常情况下通过以下取值范围判断变量的相关强度：
相关系数          0.8-1.0     极强相关
                 0.6-0.8     强相关
                 0.4-0.6     中等程度相关
                 0.2-0.4     弱相关
                 0.0-0.2     极弱相关或无相关

#### 代码实现

![输入图片说明](1675752085157.jpg)

#### 软件架构
Spring boot单项目

#### 安装教程

1.  git下载源码
2.  maven构建依赖
3.  idea-java运行
#### 使用说明

1. 找到  src / main / java / com / tarzan / recommend / RecommendSystemApplication.java  右键java 运行
![输入图片说明](1675756915170.jpg)
2.传入不同的用户id和不同的电影id，得到不同的推荐数据
            
            ------基于用户协同过滤推荐---------------下列电影
            Pulp Fiction (1994)
            While You Were Sleeping (1995)
            Four Weddings and a Funeral (1994)
            Remains of the Day, The (1993)
            Sleepless in Seattle (1993)
            Dances with Wolves (1990)
            Blues Brothers, The (1980)
            Sting, The (1973)
            Graduate, The (1967)
            Groundhog Day (1993)
            Back to the Future (1985)
            Young Frankenstein (1974)
            M*A*S*H (1970)
            When Harry Met Sally... (1989)
            Clueless (1995)
            Bridges of Madison County, The (1995)
            Muriel's Wedding (1994)
            Mrs. Doubtfire (1993)
            Ghost (1990)
            Harold and Maude (1971)
            Duck Soup (1933)
            Butch Cassidy and the Sundance Kid (1969)
            Annie Hall (1977)
            Manhattan (1979)
            Cool Hand Luke (1967)
            Great Dictator, The (1940)
            Somewhere in Time (1980)
            Being There (1979)
            Pretty Woman (1990)
            French Kiss (1995)
            Big Green, The (1995)
            ------基于物品协同过滤推荐---------------下列电影
            Guilty as Sin (1993)
            Colonel Chabert, Le (1994)

3.项目中用到的文件数据集ml-100k 在 src / main / resources目录下

#### 技术交流&问题反馈

      刚刚整理的代码还有很多不足之处，如有问题请联系我

      联系QQ:1334512682 
      微信号：vxhqqh

#### 我的博客

[洛阳泰山](https://blog.csdn.net/weixin_40986713)




