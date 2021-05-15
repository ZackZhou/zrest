package categories

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import interfaces.IRestRequest
import io.restassured.specification.RequestSpecification
import models.RestBaseModel

import static org.hamcrest.MatcherAssert.assertThat

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/8 4:54 PM
 * @description ： 扩展RestAssured的指定类，实现：　可以通过　models.RestBaseModel　的具体实现类　指定请求的具体参数
 * @modified By：
 * @version:
 */

@Category(RequestSpecification)
class RequestSpecificationExtend {
    //closure used to update the json
    RequestSpecification body(String json, Closure update) {
        def jsonObj = new JsonSlurper().parseText(json)
        body(jsonObj, update)
    }

    /*
        closure used to update the json object, the json object comes from get_store
        支持两种方式：
        1.
            given_studio()
                .use(Person_Update_Post)
                .body(get_store("personInfo")) {
                    it.person_name = person_name + "1"
                    it.idNumber = "id_" + person_name + "1"
                    it.isUpdateImage = "1"
                    it.operatePerson = "0"
                }.headers(studio_token).when().call("更新人员信息").then().assertSuccess()
        2.
             given_studio()
                .use(Person_Update_Post)
                .body(get_store("personInfo")) { Person_Update_Post request ->
                    request.update_info(person_name)
                }.headers(studio_token).when().call("更新人员信息").then().assertSuccess()
    */

    RequestSpecification body(Object jsonMap, Closure update) {
        def args_size = update.getMaximumNumberOfParameters()
        if (args_size == 1) {
            def first_args_type = update.getParameterTypes().getAt(0)

            if (first_args_type.newInstance() instanceof RestBaseModel) {
                def model = this.requestInstance as RestBaseModel
                model.setBody(JsonOutput.toJson(jsonMap))
                update(model)
                jsonMap = model.parse_body()
            } else
                update.call(jsonMap)
        } else if (args_size == 2)
            update.call(this.requestInstance, jsonMap) // 通过use 实例化的 IRestRequest 实现类的 实例
        else
            update.call()

        return body(JsonOutput.toJson(jsonMap))
    }

    RequestSpecification body(File jsonFile, Closure update) {
        def jsonObj = new JsonSlurper().parse(jsonFile)

        body(jsonObj, update)
    }

    //修改并使用默认的body
    RequestSpecification body(Class<IRestRequest> request, Closure update) {
        def requestInstance = request.newInstance()

        def jsonObj = new JsonSlurper().parseText(requestInstance.get_body())

        def args_size = update.getMaximumNumberOfParameters()
        if (args_size == 1)
            update.call(jsonObj)
        else if (args_size == 2)
            update.call(requestInstance, jsonObj)
        else
            update.call()

        return body(JsonOutput.toJson(jsonObj))
    }

    //使用默认的body
    RequestSpecification body(Class<IRestRequest> request) {
        def requestInstance = request.newInstance()
        return body(requestInstance.get_body())
    }

    //使用默认的body
    RequestSpecification body(Closure request) {
        def args_size = request.getMaximumNumberOfParameters()
        assert args_size == 1//必须1个参数

        request(this.requestInstance)

        return body((IRestRequest) this.requestInstance.get_body())
    }

    //使用默认的headers
    RequestSpecification headers(Class<IRestRequest> request) {
        def requestInstance = request.newInstance()
        return headers(requestInstance.get_headers())
    }

    //使用默认的pathParams
    RequestSpecification pathParams(Class<IRestRequest> request) {
        def requestInstance = request.newInstance()
        return pathParams(requestInstance.get_paths())
    }

    //使用默认IRestRequest中的默认值
    RequestSpecification use(Class<IRestRequest> request) {
        def requestInstance = request.newInstance()

        def requestSpecification = contentType(requestInstance.get_content_type())
                .headers(requestInstance.get_headers())
                .queryParams(requestInstance.get_queries())
                .pathParams(requestInstance.get_paths())
                .body(requestInstance.get_body())

        //实例级别别的动态属性，只有此调用链上的 RequestSpecification 实例 才有requestInstance属性
        requestSpecification.metaClass.requestInstance = requestInstance // 动态添加 属性，方便后续的 call 方法调用
        requestSpecification
    }

    //使用默认IRestRequest中的默认值
    RequestSpecification use(Class<IRestRequest> request, Closure update) {
        def requestInstance = request.newInstance()

        String body = requestInstance.get_body()

        def body_obj = new JsonSlurper().parseText(body)

        def args_size = update.getMaximumNumberOfParameters()
        if (args_size == 1)
            update.call(body_obj)
        else if (args_size == 2)
            update.call(requestInstance, body_obj)
        else
            update.call()

        def requestSpecification = contentType(requestInstance.get_content_type())
                .headers(requestInstance.get_headers())
                .queryParams(requestInstance.get_queries())
                .pathParams(requestInstance.get_paths())
                .body(JsonOutput.toJson(body_obj))

        //实例级别别的动态属性，只有此调用链上的 RequestSpecification 实例 才有requestInstance属性
        requestSpecification.metaClass.requestInstance = requestInstance // 动态添加 属性，方便后续的 call 方法调用
        requestSpecification
    }

    //使用默认IRestRequest中的默认值
    RequestSpecification use(Closure update) {
        def args_size = update.getMaximumNumberOfParameters()
        assert args_size <= 2 //必须1到2个参数

        def first_args_type = update.getParameterTypes().getAt(0)

        def requestInstance = first_args_type.newInstance()

        assert requestInstance instanceof IRestRequest //强制要求是IRestRequest
        def body_obj = null
        String body = requestInstance.get_body()
        if (args_size == 1) {
            update.call(requestInstance)
        } else {
            body_obj = body ? new JsonSlurper().parseText(body) : [:] // body 为空 则判断结果为false, 故 body_obj = [:]
            update.call(requestInstance, body_obj)
        }

        def requestSpecification = contentType(requestInstance.get_content_type())
                .headers(requestInstance.get_headers())
                .queryParams(requestInstance.get_queries())
                .pathParams(requestInstance.get_paths())

        if (args_size == 1)
            requestSpecification.body(requestInstance.get_body())
        else
            requestSpecification.body(!body ? body : JsonOutput.toJson(body_obj)) // 如果body 为空,就使用空,否则使用更新后的json object

        //实例级别别的动态属性，只有此调用链上的 RequestSpecification 实例 才有requestInstance属性
        requestSpecification.metaClass.requestInstance = requestInstance // 动态添加 属性，方便后续的 call 方法调用
        requestSpecification
    }

    //扩展port接口，支持string
    RequestSpecification port(def port) {
        try {
            int iPort = Integer.parseInt(port)

            port(iPort)
        }
        catch (Exception e) {
            assertThat("获取接口失败", false)
            return this
        }
    }
}
