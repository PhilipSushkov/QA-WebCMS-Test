package pageobjects.LiveSite;

import org.openqa.selenium.*;
import pageobjects.AbstractPageObject;
import pageobjects.Dashboard.Dashboard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static specs.AbstractSpec.propUIPublicSite;

/**
 * Created by philipsushkov on 2016-11-09.
 */
public class LiveEvents extends AbstractPageObject {

    private final By headline;
    private final By eventTitle;
    private final By eventDate;
    private final By pastEvents;

    // elements on page of loaded event
    private final By eventImage;
    private final By eventDetailsModule;
    private final By eventDateRange;

    public LiveEvents(WebDriver driver) {
        super(driver);
        headline = By.xpath(propUIPublicSite.getProperty("headline"));
        eventTitle = By.className(propUIPublicSite.getProperty("eventTitle"));
        eventDate = By.className(propUIPublicSite.getProperty("eventDate"));
        pastEvents = By.linkText(propUIPublicSite.getProperty("pastEvents"));

        // elements on page of loaded event
        eventImage = By.xpath(propUIPublicSite.getProperty("eventImage"));
        eventDetailsModule = By.className(propUIPublicSite.getProperty("eventDetailsModule"));
        eventDateRange = By.className(propUIPublicSite.getProperty("eventDateRange"));
    }

    public Dashboard dashboard(String url) {
        driver.get(url);
        return new Dashboard(getDriver());
    }

    public boolean canFindNewHeadline(String expectedHeadline, boolean desiredState, String[] expectedFilenames) throws InterruptedException {
        WebElement headlines;
        int refreshAttempts = 0;
        boolean foundHeadline = !desiredState;
        long startTime = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

        while (foundHeadline!=desiredState && time-startTime<120000){
            try {
                Thread.sleep(5000);
            }
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
                headlines = driver.findElement(headline);

                System.out.println("Found Head Line: "+headlines.getText());
                if (!headlines.getText().equals(expectedHeadline)){
                    foundHeadline = false;
                    System.out.println("ERROR: Head Line doesn't match.");
                } else {
                    foundHeadline = true;
                }

                String[] foundFilenames = new String[2];
                foundFilenames[0] = findElement(eventImage).getAttribute("src");
                System.out.println("Found image file: "+foundFilenames[0]);
                if (!foundFilenames[0].contains(expectedFilenames[0])){
                    foundHeadline = false;
                    System.out.println("ERROR: Image filename doesn't match.");
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

    public boolean eventsAreDisplayed(){
        return doesElementExist(eventTitle) && findElement(eventTitle).isDisplayed();
    }

    public boolean allEventsAreUpcoming(){
        boolean allUpcoming = true;
        Date current = new Date();
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy H:m:s", Locale.US);
        List<WebElement> eventTitles = findElements(eventTitle);
        List<WebElement> eventDates = findElements(eventDate);
        for (int i=0; i<eventDates.size(); i++){
            Date date;
            try {
                date = format.parse(eventDates.get(i).getText()+" 23:59:59"); // any event that occurred today counts as upcoming
            }catch (ParseException e){
                System.out.println("Event with title: "+eventTitles.get(i).getText()+"\n\thas invalid date: "+eventDates.get(i).getText());
                return false;
            }
            if (date.before(current)){
                // if event started before today, open event page to check that end date is today or later (in which case it still counts as upcoming)
                System.out.println("Checking end date of event with title: "+eventTitles.get(i).getText());
                eventTitles.get(i).click();
                String dateRange = findElement(eventDateRange).getText();
                dateRange = dateRange.substring(dateRange.indexOf("to")+3); // only taking ending date
                Date endDate;
                try {
                    endDate = format.parse(dateRange+" 23:59:59");
                }catch (ParseException e){
                    System.out.println("Event has invalid end date: "+eventDates.get(i).getText());
                    return false;
                }
                if (endDate.before(current)){
                    // if end date is before today, THEN the event should not be upcoming
                    System.out.println("Event has non-upcoming end date: "+dateRange);
                    allUpcoming = false;
                }
                // returning to Events page after checking end date
                driver.navigate().back();
                eventTitles = findElements(eventTitle);
                eventDates = findElements(eventDate);
            }
        }
        return allUpcoming;
    }

    public boolean allEventsArePast(){
        boolean allPast = true;
        Date current = new Date();
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy H:m:s", Locale.US);
        List<WebElement> eventTitles = findElements(eventTitle);
        List<WebElement> eventDates = findElements(eventDate);
        for (int i=0; i<eventDates.size(); i++){
            Date date;
            try {
                date = format.parse(eventDates.get(i).getText()+" 23:59:59");
            }catch (ParseException e){
                System.out.println("Event with title: "+eventTitles.get(i).getText()+"\n\thas invalid date: "+eventDates.get(i).getText());
                return false;
            }
            if (date.after(current)){
                System.out.println("Event with title: "+eventTitles.get(i).getText()+"\n\thas non-past date: "+eventDates.get(i).getText());
                allPast = false;
            }
        }
        return allPast;
    }

    public void switchToPastEvents(){
        findElement(pastEvents).click();
    }

    public void openFirstEvent(){
        findElement(eventTitle).click();
    }

    public boolean eventIsOpen(){
        return doesElementExist(eventDetailsModule) && findElement(eventDetailsModule).isDisplayed();
    }
}
