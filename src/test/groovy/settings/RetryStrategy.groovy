package settings

import groovy.util.logging.Log4j
import org.testng.IRetryAnalyzer
import org.testng.ITestResult
import test.TestBase

import java.util.concurrent.atomic.AtomicInteger

/**
 * @author ：ZackZhou
 * @date ：Created in 2020/5/25 3:55 PM
 * @description ： TestNG重试失败的用例
 * @modified By：
 * @version:
 */

@Log4j
class RetryStrategy implements IRetryAnalyzer {
    private static int retryMaxCount = 2

    private final Map<String, AtomicInteger> counts = new HashMap<>()

    int alreadyRetryCount = 0

    private String getCurrentCaseName(ITestResult result) {
        String caseName = result.getTestName()
        return caseName
    }

    private AtomicInteger getCount(ITestResult result) {
        String caseName = getCurrentCaseName(result)
        AtomicInteger count = counts.get(caseName)
        if (count == null) {
            count = new AtomicInteger(retryMaxCount)
            counts.put(caseName, count)
        }
        return count
    }

    private void setCount(ITestResult result, int remainCount) {
        String caseName = getCurrentCaseName(result)
        AtomicInteger count = counts.get(caseName)
        if (count != null) {
            count = new AtomicInteger(remainCount)
            counts.put(caseName, count)
        }
    }

    @Override
    boolean retry(ITestResult result) {
        String maxCountStr = System.getProperty('max_retry')
        retryMaxCount = Integer.parseInt(maxCountStr)

        int retriesRemaining = getCount(result).decrementAndGet()
        alreadyRetryCount = retryMaxCount - retriesRemaining
        if (retriesRemaining >= 0) {
            if (result.instance instanceof TestBase) {
                int stableSleep = Integer.parseInt(System.getProperty('retry_interval'))
                sleep(stableSleep)
            }

            setCount(result, retriesRemaining)

            log.info("失败后 重试一次")
            return true
        }

        return false
    }
}
