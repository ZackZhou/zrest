package categories

import groovy.json.JsonSlurper
import io.restassured.response.ValidatableResponseOptions
import org.hamcrest.Matcher

import static org.hamcrest.MatcherAssert.assertThat

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/15 9:29 PM
 * @description ： 对校验方式的扩展
 * @modified By：
 * @version:
 */

@Category(ValidatableResponseOptions)
class ValidatableResponseOptionsExtend {
    ValidatableResponseOptions body(Matcher matcher, Closure closure) {
        def response = this.extract().asString()
        def responseObj = new JsonSlurper().parseText(response)

        try {
            def result = closure.call(responseObj)

            assertThat(result, matcher)
            return this
        }
        catch (Exception e) {
            assertThat("返回截取失败", false)
            return this
        }
    }

    //允许在　Closure中进行多步骤校验
    ValidatableResponseOptions body(Closure assertions) {
        def response = this.extract().asString()
        def responseObj = new JsonSlurper().parseText(response)

        try {
            assertions.call(responseObj)

            return this
        }
        catch (Exception e) {
            assertThat("返回截取失败", false)
            return this
        }
    }
}
