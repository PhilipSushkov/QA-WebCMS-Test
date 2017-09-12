package util;

/**
 * Created by philipsushkov on 2016-11-29.
 */

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalDriverFactory {
    public static final long DEFAULT_TIMEOUT = 5L;

    public static WebDriver createInstance(String browserName) {
        WebDriver driver = null;
        //Proxy selProxy = ClientUtil.createSeleniumProxy(proxy);

        switch (browserName) {
            case "chrome":

                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("incognito");
                options.addArguments("no-sandbox");
                //capabilities.setCapability(ChromeOptions.CAPABILITY, options);

                //capabilities.setCapability(CapabilityType.PROXY, selProxy);
                //capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

                //LoggingPreferences loggingprefs = new LoggingPreferences();
                //loggingprefs.enable(LogType.BROWSER, Level.ALL);
                //capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);

                driver = new ChromeDriver(capabilities);

                driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS); //Increased to 20 to perhaps reduce timeouts?

                return driver;

            case "phantom":

                Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);

                DesiredCapabilities caps = new DesiredCapabilities();
                String[] phantomArgs = new  String[] {"--web-security=no", "--ignore-ssl-errors=yes", "--webdriver-loglevel=NONE"};
                caps.setJavascriptEnabled(true);
                caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
                driver = new PhantomJSDriver(caps);

                driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(45, TimeUnit.SECONDS); //Increased to 20 to perhaps reduce timeouts?

                return driver;

            default:
                return driver;
        }

    }

}
