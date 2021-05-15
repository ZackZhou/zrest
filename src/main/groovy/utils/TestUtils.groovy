package utils

import groovy.util.logging.Log4j
import io.qameta.allure.Allure
import io.qameta.allure.Step

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * @author ：ZackZhou
 * @date ：Created in 2021/1/8 4:54 PM
 * @description ：
 * @modified By：
 * @version:
 */


@Log4j
class TestUtils {
    @Step("Sleep {sec} 秒")
    static cold_down(int sec) {
        log.info("休息${sec}秒")
        sleep(sec * 1000)
    }

    //filepath under resource folder i.e. files/files/references/baggage.png
    static void attach_screenshot(String filePath) {
        InputStream inputStream = new FileInputStream(resource(filePath))
        Allure.addAttachment("known_issue", inputStream)
    }

    static File resource(String resourceFilePath) {
        String resourceDir = System.properties.get("user.dir") + "/src/test/resources/"
        File file = new File("${resourceDir}/${resourceFilePath}")
        return file
    }

    static String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
        Date date = new Date()
        String currentTime = dateFormat.format(date)
        return currentTime
    }
}