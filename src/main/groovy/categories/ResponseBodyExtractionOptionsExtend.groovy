package categories

import groovy.json.JsonSlurper
import interfaces.IRestStore
import io.restassured.response.ResponseBodyExtractionOptions

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/8 4:54 PM
 * @description ： 扩展RestAssured的指定类，用来链式存储json结果中的值到变量中
 * @modified By：
 * @version:
 */

@Category(ResponseBodyExtractionOptions)
class ResponseBodyExtractionOptionsExtend {
    //usage: store(key:"key_name", path:"jsonpath",storeObject)
    ResponseBodyExtractionOptions store(Map<String, String> key_path, IRestStore store) {
        store.set_store(key_path.get("key"), path(key_path.get("path")))
        return this
    }

    //usage: store(key:"key_name", storeObject) { it.data }
    ResponseBodyExtractionOptions store(String key, IRestStore store, Closure parser) {
        def json_obj = new JsonSlurper().parseText(this.asString())
        def value = parser.call(json_obj)

        store.set_store(key, value)
        return this
    }
}
