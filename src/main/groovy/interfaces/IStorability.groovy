package interfaces

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/8 4:54 PM
 * @description ： 用来表示拥有存储json消息功能的类
 * @modified By：
 * @version:
 */

interface IStorability {
    def get_store(String key)

    def set_store(String key, Object value)
}