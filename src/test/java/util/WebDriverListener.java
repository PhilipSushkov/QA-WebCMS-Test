package util;

/**
 * Created by philipsushkov on 2016-11-29.
 */

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class WebDriverListener implements IInvokedMethodListener {
    //public BrowserMobProxy proxy = new BrowserMobProxyServer();

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

        if (method.isTestMethod()) {
            WebDriver driver = null;
            String browserName = method.getTestMethod().getXmlTest().getLocalParameters().get("browserName");

            //proxy.start(0);

            try {
                //driver = LocalDriverFactory.createInstance(browserName, proxy);
                driver = LocalDriverFactory.createInstance(browserName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            LocalDriverManager.setWebDriver(driver);
            //LocalDriverManager.setProxy(proxy);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            WebDriver driver = LocalDriverManager.getDriver();
            if (driver != null) {
                driver.quit();
                //proxy.stop();
            }
        }
    }
}
