package settings

import groovy.util.logging.Log4j
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.config.ParamConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.config.SSLConfig
import io.restassured.specification.RequestSpecification

import static io.restassured.RestAssured.defaultParser
import static io.restassured.RestAssured.given
import static io.restassured.parsing.Parser.JSON

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/12 1:56 PM
 * @description ： 用来设置各种变量
 * @modified By：
 * @version:
 */

@Log4j
class Givens {
    static {
        defaultParser = JSON
        read_properties()
    }

    Givens() {}

    //读取gradle.properties 文件里面的属性定义,当项目不是从gradle环境启动时生效
    private static void read_properties() {
        def projectDir = System.getProperty('user.dir')
        Properties properties = new Properties()
        properties.load(new FileInputStream(projectDir + "/gradle.properties"))

        properties.each { key, value ->
            if (System.hasProperty(key.toString()))
                log.info("已经有${key}属性")
            else {
                System.setProperty(key.toString(), value.toString())
                log.info("设置系统属性 ${key}:${value}")
            }
        }

        System.properties.setProperty('log4j.configuration', projectDir + "/src/test/resources/log4j.properties")
    }

    //用来设置参数更新策略,比如　原来的　headers map 里面已经有一个key,value, 再一次设置这个key的值时，　使用的是merge 还是　replace 原来值
    RequestSpecification given_general() {
        given().config(RestAssuredConfig.newConfig()
            .paramConfig(new ParamConfig(ParamConfig.UpdateStrategy.REPLACE
                , ParamConfig.UpdateStrategy.REPLACE
                , ParamConfig.UpdateStrategy.REPLACE))
            .sslConfig(new SSLConfig().relaxedHTTPSValidation())) // no need for ssl validate by default
            .filter(new AllureRestAssured()) // 使得Allure 能够获取　请求的参数与返回信息
            .log().all()
    }

    //用来定义一些常用的请求设置
    RequestSpecification given_host() { //默认使用 json,如果不同请求使用不同类型,需要特别指定
        given_general().baseUri(System.getProperty('protocol') + "://" + System.getProperty('host'))
    }
}
