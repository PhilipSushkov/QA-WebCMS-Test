package pageobjects.LiveSite;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pageobjects.AbstractPageObject;
import pageobjects.Dashboard.Dashboard;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static specs.AbstractSpec.propUIPublicSite;

public class LivePressReleases extends AbstractPageObject{

    private final By latestHeadlines;
    private final By latestHeadlineLinks;
    private final By latestHeadlines_PressRelease;
    private final By pressReleaseDate;
    private final By yearLink_PressRelease;

    // elements on page of loaded press release
    private final By pressReleaseImage;
    private final By documentDownloadLink_PressRelease;
    private final By DownloadLink;

    public LivePressReleases(WebDriver driver) {
        super(driver);
        latestHeadlines = By.xpath(propUIPublicSite.getProperty("latestHeadlines"));
        latestHeadlineLinks = By.xpath(propUIPublicSite.getProperty("latestHeadlineLinks"));
        latestHeadlines_PressRelease = By.xpath(propUIPublicSite.getProperty("latestHeadlines_PressRelease"));
        pressReleaseDate = By.className(propUIPublicSite.getProperty("pressReleaseDate"));
        yearLink_PressRelease = By.className(propUIPublicSite.getProperty("yearLink_PressRelease"));
        DownloadLink = By.xpath("//a[@id='_ctrl0_ctl45_hrefDownload']");

        // elements on page of loaded press release
        pressReleaseImage = By.xpath(propUIPublicSite.getProperty("pressReleaseImage"));
        documentDownloadLink_PressRelease = By.xpath(propUIPublicSite.getProperty("documentDownloadLink_PressRelease"));
    }

    public Dashboard dashboard(String url) {
        driver.get(url);
        return new Dashboard(getDriver());
    }

    public boolean canFindNewHeadline2(String expectedHeadline, boolean desiredState, String[] expectedFilenames){
        List<WebElement> headlines;
        List<WebElement> headlineLinks;
        int refreshAttempts = 0;
        boolean foundHeadline = !desiredState;
        long startTime = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

        while (foundHeadline!=desiredState && time-startTime<120000){
            try{Thread.sleep(5000);}
            catch(InterruptedException e){
                e.printStackTrace();
            }
            refreshAttempts++;
            System.out.println("Now performing refresh "+refreshAttempts);
            time = System.currentTimeMillis();
            try {
                driver.navigate().refresh();
            } catch (TimeoutException e) {
                driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
            }

            headlines = findElements(latestHeadlines_PressRelease);

            for (int i=0; i<headlines.size(); i++){
                System.out.println("HEADLINE: "+headlines.get(i).getText());
                if (headlines.get(i).getText().equalsIgnoreCase(expectedHeadline)) {
                    foundHeadline = true;
                    if(desiredState){
                        try {
                            headlines.get(i).click();
                        } catch (TimeoutException e) {
                            driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
                        }
                        wait.until(ExpectedConditions.visibilityOf(findElements(pressReleaseImage).get(0)));
                        String[] foundFilenames = new String[2];
                        foundFilenames[0] = findElements(pressReleaseImage).get(0).getAttribute("src");
                        foundFilenames[0] = foundFilenames[0].substring(foundFilenames[0].indexOf("files/")+6);
                        System.out.println("Found image file: "+foundFilenames[0]);
                        if (!foundFilenames[0].equals(expectedFilenames[0])){
                            foundHeadline = false;
                            System.out.println("ERROR: Image filename doesn't match.");
                        }
                        foundFilenames[1] = findElements(documentDownloadLink_PressRelease).get(0).getAttribute("href");
                        foundFilenames[1] = foundFilenames[1].substring(foundFilenames[1].indexOf("files/")+6);
                        System.out.println("Found attached document: "+foundFilenames[1]);
                        if (!foundFilenames[1].equals(expectedFilenames[1])){
                            foundHeadline = false;
                            System.out.println("ERROR: Attached document filename doesn't match.");
                        }
                    }
                    break;
                }
                else {
                    foundHeadline = false;
                }
            }
        }

        return foundHeadline;
    }


    public boolean canFindNewHeadline(String expectedHeadline, boolean desiredState, String[] expectedFilenames) throws InterruptedException {
        WebElement headlines;
        int refreshAttempts = 0;
        boolean foundHeadline = !desiredState;
        long startTime = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

        while (foundHeadline!=desiredState && time-startTime<120000){
            try{Thread.sleep(5000);}
            catch(InterruptedException e){
                e.printStackTrace();
            }
            refreshAttempts++;
            System.out.println("Now performing refresh "+refreshAttempts);
            time = System.currentTimeMillis();

            try {
                driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
                Thread.sleep(1000);
                driver.navigate().refresh();
            } catch (TimeoutException e) {
                driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
            }

            try {
                driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
                headlines = driver.findElement(latestHeadlines);

                System.out.println("Found Head Line: "+headlines.getText());
                if (!headlines.getText().equals(expectedHeadline)){
                    foundHeadline = false;
                    System.out.println("ERROR: Head Line doesn't match.");
                } else {
                    foundHeadline = true;
                }

                String[] foundFilenames = new String[2];
                foundFilenames[0] = findElement(pressReleaseImage).getAttribute("src");
                System.out.println("Found image file: "+foundFilenames[0]);
                if (!foundFilenames[0].contains(expectedFilenames[0])){
                    foundHeadline = false;
                    System.out.println("ERROR: Image filename doesn't match.");
                }

                foundFilenames[1] = findElement(documentDownloadLink_PressRelease).getAttribute("src");
                System.out.println("Found image file: "+foundFilenames[0]);
                if (!foundFilenames[0].contains(expectedFilenames[0])){
                    foundHeadline = false;
                    System.out.println("ERROR: Attached document filename doesn't match.");
                }

            } catch (ElementNotFoundException e2) {
                foundHeadline = false;
            } catch (TimeoutException e3) {
                driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
                foundHeadline = false;
            } catch (NoSuchElementException e4) {
                driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
                foundHeadline = false;
            }
        }
        return foundHeadline;
    }

    // NEW METHODS CREATED FOR PUBLIC SITE SMOKE TEST

    public boolean pressReleasesAreDisplayed(){
        return doesElementExist(latestHeadlines_PressRelease) && findElement(latestHeadlines_PressRelease).isDisplayed();
    }

    public boolean pressReleasesAreAllFromYear(String year){
        boolean allFromYear = true;
        List<WebElement> headlines = findElements(latestHeadlines_PressRelease);
        List<WebElement> pressReleaseDates = findElements(pressReleaseDate);
        for (int i=0; i<pressReleaseDates.size(); i++){
            if (!pressReleaseDates.get(i).getText().contains(year)){
                System.out.println("Press release with headline: "+headlines.get(i).getText()+"\n\thas date "+pressReleaseDates.get(i).getText()+" not in "+year);
                allFromYear = false;
            }
        }
        return allFromYear;
    }

    public void switchYearTo(String year){
        List<WebElement> yearLinks = findElements(yearLink_PressRelease);
        for (int i=0; i<yearLinks.size(); i++){
            if (yearLinks.get(i).getText().equals(year)){
                yearLinks.get(i).click();
                return;
            }
        }
    }

    public void openFirstPressRelease(){
        findElement(latestHeadlines_PressRelease).click();
    }

    public boolean pressReleaseIsOpen(){
        return doesElementExist(documentDownloadLink_PressRelease) && findElement(documentDownloadLink_PressRelease).isDisplayed();
    }

    public  void ClickLatestRelease() {
        findElement(latestHeadlines_PressRelease).click();

    }

    public boolean FindDownloadLink() {
        return doesElementExist(DownloadLink) && findElement(DownloadLink).isDisplayed();
    }
}
