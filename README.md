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


class TestCase extends TestBase {
    @Delegate
    Givens givens = new Givens() // 可以放到新建的测试基类中

    @Test(description = '创建并获取用户', retryAnalyzer = RetryStrategy)
    void testcase_01() {
        use(categories) {
            given_host() // 此处只有一个host,如果有多个host 交互，可以在Givens中新建另一个host, 如　given_host_a()
                .use(Create_Post) { it.name = 'Zack' } //修改请求体里面的值
                .when().call('创建用户，并校验接口结果')
                .then().assertThat()
                .statusCode(200)
                .body { assert it.status == 'Awesome!' }
                .extract().store('id', this) { it.id } // 将id 存起来

            given_host()
                .use(List_Get)
                .when().call('获取所有用户')
                .then().assertThat()
                .statusCode(200)
                .body {
                    assert it.data.id.contains(get_store('id')) //验证新建的id存在
                    assert it.data.name.contains('Zack') //验证第一步的用户名存在
                }

            given_host()
                .use(Info_Post)
                .queryParams("id": get_store('id')) //获取ID对应用户的详细信息
                .when().call('获取所有用户')
                .then().assertThat()
                .statusCode(200)
                .body { assert it.info == 'my name is Zack' }
        }
    }
}