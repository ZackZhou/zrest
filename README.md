# zrest
扩展的restassured接口自动化框架

用到的组件:
1. RestAssured
2. TestNG
3. AllureReport
4. Groovy(MOP)
5. Gradle

clone 到本地后，命令行运行
1. ./gradlew User
2. ./gradlew allureReport
3. ./gradlew allureServe

浏览器查看报告。

如何添加用例：
1. 根据情况修改　gradle.properties 中的　host
2. 将接口抽象成模型类加到：　src/test/groovy/models下面。这一步可以通过自动化工具，将所有接口一次性加到此package下。　如：解析swagger得到所有请求模型
3. src/test/groovy/testcases下面添加测试用例

报告示例：
![image](https://github.com/ZackZhou/zrest/blob/main/images/Graphs.png)
![image](https://github.com/ZackZhou/zrest/blob/main/images/Packages.png)

用例格式：

![image](https://github.com/ZackZhou/zrest/blob/main/images/Case_Structure.png)
