package testcases

import models.Create_Post
import models.Info_Post
import models.List_Get
import org.testng.annotations.Test
import settings.Givens
import settings.RetryStrategy
import test.TestBase

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
