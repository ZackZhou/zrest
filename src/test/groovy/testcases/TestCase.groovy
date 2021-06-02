package testcases

import settings.Givens
import test.TestBase

class TestCase extends TestBase {
    @Delegate
    Givens givens = new Givens() // 代理给Givens做设置

    /*
        公用步骤/方法可以放到此父类
    * */
}
