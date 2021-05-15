package test

import categories.RequestSenderOptionsExtend
import categories.RequestSpecificationExtend
import categories.ResponseBodyExtractionOptionsExtend
import categories.ValidatableResponseOptionsExtend
import groovy.util.logging.Log4j
import interfaces.IStorability
import org.testng.Assert
import utils.GlobalStore

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/8 3:18 PM
 * @description ： 所有测试的基类
 * @modified By：
 * @version:
 */

//保持测试父类的功能单一性
@Log4j
class TestBase implements IStorability {
    //打开category， 使用自定义的RestAssured扩展方法
    List<Class> categories = [RequestSenderOptionsExtend, RequestSpecificationExtend, ResponseBodyExtractionOptionsExtend, ValidatableResponseOptionsExtend]

    //used to store step values in testcase
    protected Map stores = new HashMap<String, Object>()

    //used to store values among testcases
    protected GlobalStore globalStore = new GlobalStore()

    @Override
    def get_store(String key) {
        //assert the key is not null
        Assert.assertNotNull(key)
        def value = this.stores.get(key)
        log.info("获取 ${key}: ${value}")
        //assert the value is not null
        Assert.assertNotNull(value, "the value of key '${key}' not exist!! Check the response of previous api, or check the definition of key '${key}' ! ")
        return value
    }

    @Override
    def set_store(String key, Object value) {
        this.stores.put(key, value)
    }
}
