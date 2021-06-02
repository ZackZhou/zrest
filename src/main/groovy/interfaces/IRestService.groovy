package interfaces

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/8 4:54 PM
 * @description ： 用来规范服务的语法。 如mysql, redis校验服务
 * @modified By：
 * @version:
 */

interface IRestService {
    //方法调用
    IRestService call(String command, String description)

    IRestService waitFor(int secs, String description, Closure closure)

    //语法糖
    IRestService then()

    //用来验证call的返回
    IRestService body(Closure closure)

    //用来存放中间变量，方便链式编程
    IRestService store(String key, IRestStore store, Closure closure)
}