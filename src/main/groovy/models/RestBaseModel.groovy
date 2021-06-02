package models

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import interfaces.IRestRequest

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/8 10:53 PM
 * @description ： 所有请求模型类的基类
 * @modified By：
 * @version:
 */

abstract class RestBaseModel implements IRestRequest {
    protected JsonSlurper jsonSlurper = new JsonSlurper()
    String method = ""

    String content_type = "application/json"

    String url = ""

    String baseUrl = ""

    String body = ""

    Map<String, String> headers = new HashMap<>()

    Map<String, String> queries = new HashMap<>()

    Map<String, String> paths = new HashMap<>()

    @Override
    String get_method() {
        return this.method.toLowerCase()
    }

    @Override
    Map<String, String> get_headers() {
        return this.headers
    }

    @Override
    Map<String, String> get_queries() {
        return this.queries
    }

    @Override
    Map<String, String> get_paths() {
        return this.paths
    }

    @Override
    String get_url() {
        return (baseUrl + url)
    }

    @Override
    String get_body() {
        return this.body
    }

    /**
     * create by: ZackZhou
     * description: DSL 用来更新Body
     *          usage:
     *              body {*                  it.key = value
     *}*
     * create time: 2021/3/17 下午9:52
     *
     * @param null
     * @return
     */
    void body(Closure update) {
        def body_obj = parse_body()
        update(body_obj)
        this.setBody(JsonOutput.toJson(body_obj))
    }

    /**
     * create by: ZackZhou
     * description: 用来简化 json的解析
     *  usage:
     *      配合body 使用:
     *      body {*          it.complexMap = json """
     *{*                    "monitoringPolicyUuid": "",
     *                    "operatePerson": "0",
     *                    "did": "${did1}",
     *                     "regionPicUrl": "${url1}",
     *}*          """
     *}* create time: 2021/3/17 下午11:01
     *
     * @param null
     * @return
     */
    def json(String json) {
        jsonSlurper.parseText(json)
    }

    @Override
    String get_content_type() {
        return this.content_type
    }

    @Override
    String toString() {
        return "Call ${this.url}"
    }

    @Override
    String pretty_print() {
        """
            URL: ${get_url()}
            Method: ${get_method()}
            Headers: ${get_headers().toMapString()}
            Queries: ${get_queries().toMapString()}
            Paths: ${get_paths().toMapString()}
            Body: ${get_body()}
        """
    }

    def parse_body() {
        jsonSlurper.parseText(get_body())
    }
}
