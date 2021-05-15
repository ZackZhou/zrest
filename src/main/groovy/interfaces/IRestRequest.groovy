package interfaces

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/8 4:54 PM
 * @description ： 用来指定Rest 接口需要实现的方法
 * @modified By：
 * @version:
 */

interface IRestRequest {
    //post,get,update,delete,and on
    String get_method()

    Map<String, String> get_headers()

    Map<String, String> get_queries()

    Map<String, String> get_paths()

    String get_url()

    String get_body()

    String pretty_print()

    String get_content_type()
}