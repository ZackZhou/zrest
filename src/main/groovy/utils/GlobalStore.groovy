package utils

import groovy.util.logging.Log4j
import interfaces.IRestStore
import org.testng.Assert

/**
 * @author ：ZackZhou
 * @date ：Created in 2020/6/1 6:47 PM
 * @description ：
 * @modified By：
 * @version:
 */

@Log4j
class GlobalStore implements IRestStore {
    //used to store step values
    protected Map stores = new HashMap<String, Object>()

    def get_store(String key) {
        //assert the key is not null
        Assert.assertNotNull(key)
        def value = this.stores.get(key)
        log.info("获取 ${key}: ${value}")
        //assert the value is not null
        Assert.assertNotNull(value, "the value of key '${key}' not exist!! Check the response of previous api, or check the definition of key '${key}' ! ")
        return value

    }

    def set_store(String key, Object value) {
        this.stores.put(key, value)
    }
}
