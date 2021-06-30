package categories

import groovy.util.logging.Log4j
import interfaces.IRestRequest
import io.qameta.allure.Allure
import io.qameta.allure.Step
import io.qameta.allure.model.Parameter
import io.restassured.config.HeaderConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.config.SSLConfig
import io.restassured.response.Response
import io.restassured.specification.RequestSenderOptions
import io.restassured.specification.RequestSpecification
import utils.TestUtils

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/8 4:54 PM
 * @description ： 扩展RestAssured的指定类，根据models.RestBaseModel类的请求方式(method)发送相应的请求
 * @modified By：
 * @version:
 */

@Log4j
@Category(RequestSenderOptions)
class RequestSenderOptionsExtend {
    //公用的更新allure report 中的步骤信息更新操作
    void allure_step_update(description, start_date, end_date) {
        Allure.lifecycle.updateStep { stepResult ->
            stepResult.name = description

            stepResult.parameters.clear() //清除原有值

            Parameter start_param = new Parameter()
            start_param.name = 'start'
            start_param.value = start_date

            stepResult.parameters.add(start_param)

            Parameter end_param = new Parameter()
            end_param.name = 'end'
            end_param.value = end_date

            stepResult.parameters.add(end_param)
        }
    }

    /**
     * create by: ZackZhou
     * description: 此方法用来允许 用户 使用 restassured 提供的 header 相关方法 覆盖 model 中的header值, 注意： 其他 query, path, formPrama 的设置策略 放在Given类中，由restassured 本身提供的功能进行设置
     * create time: 2021/4/22 11:25 AM
     *
     * @return
     */
    void headers_overwrite(Map pre_headers) {
        def header_names = pre_headers.keySet().toList()
        if (header_names.size() > 0) {
            assert this instanceof RequestSpecification
            (this as RequestSpecification).given().config(RestAssuredConfig.newConfig()
                .headerConfig(new HeaderConfig().overwriteHeadersWithName(*header_names))
                .sslConfig(new SSLConfig().relaxedHTTPSValidation()) // no need for ssl validate by default
            )
        }
    }

    //发送请求 - 公用方法
    Response send_request(IRestRequest request, String description) {
        headers_overwrite(request.get_headers()) // 允许覆盖headers
        String start_date = TestUtils.getCurrentDateTime()
        Response response = "${request.get_method()}"(request.get_url())

        String end_date = TestUtils.getCurrentDateTime()

        response.then().log().everything(true).toString() // 将结果输出到控制台
        allure_step_update(description, start_date, end_date)
        response
    }

    @Step("{description}")
    Response call(String description) {
        log.info("STEP: " + description)

        IRestRequest request = this.requestInstance //获取 通过use 方法动态添加的请求实例，来源： RequestSpecificationExtend.use()
        Response response = send_request(request, description)
        int sleep_after_call = Integer.parseInt(System.getProperty('wait_every_call'))
        sleep(sleep_after_call)
        log.info("休息 ${sleep_after_call} 毫秒")
        response
    }

    @Step("{description}")
    Response call(Class<IRestRequest> requestClass, String description) {
        log.info("STEP: " + description)

        def request = requestClass.newInstance()
        send_request(request, description)
    }

    //是否将base_url去掉，使用场景为：通过IP + Port 调用接口,绕过 gateway
    @Step("{description}")
    Response call(Class<IRestRequest> requestClass, String description, boolean ignore_base_url) {
        log.info("STEP: " + description)

        def request = requestClass.newInstance()

        //是否将base_url去掉，使用场景为：通过IP + Port 调用接口,绕过 gateway
        if (ignore_base_url)
            request.baseUrl = ""
        send_request(request, description)
    }

    //是否将base_url去掉，使用场景为：通过IP + Port 调用接口,绕过 gateway
    @Step("{description}")
    Response call(String description, boolean ignore_base_url) {
        log.info("STEP: " + description)

        IRestRequest request = this.requestInstance

        //是否将base_url去掉，使用场景为：通过IP + Port 调用接口,绕过 gateway
        if (ignore_base_url)
            request.baseUrl = ""
        send_request(request, description)
    }

    //调用的时候,可以动态修改 requestClass 里面的url, 包括但不限baseUrl
    @Step("{description}")
    Response call(Class<IRestRequest> requestClass, String description, Closure updateRequestClass) {
        log.info("STEP: " + description)

        def request = requestClass.newInstance()
        //更新requestClass实例中的元素值，如baseUrl
        updateRequestClass(request)
        send_request(request, description)
    }

    //调用的时候,可以动态修改 requestClass 里面的url, 包括但不限baseUrl
    @Step("{description}")
    Response call(String description, Closure updateRequestClass) {
        log.info("STEP: " + description)

        IRestRequest request = this.requestInstance
        //更新requestClass实例中的元素值，如baseUrl
        updateRequestClass(request)
        send_request(request, description)
    }
}
