package pageobjects.LiveSite;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.openqa.selenium.*;
import pageobjects.AbstractPageObject;

import javax.mail.Message;
import javax.mail.MessagingException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static specs.AbstractSpec.propUIPublicSite;
import static util.Functions.getSpecificMail;

/**
 * Created by kelvint on 11/17/16.
 */
public class EmailAlertsPage extends AbstractPageObject {

    private final By subscribeTitle;
    private final By emailSubTextField, pressReleaseBtn, testListBtn, eodBtn, submitBtn, submitMessage, errorMessage;
    private final By emailSubTextField_43, pressReleaseBtn_43, testListBtn_43, eodBtn_43, submitBtn_43, submitMessage_43, errorMessage_43;
    private final By emailUnsubTextField, unsubscribeBtn, unsubscribeMessage, successMessage;

    public EmailAlertsPage(WebDriver driver) {
        super(driver);

        subscribeTitle = By.xpath(propUIPublicSite.getProperty("subscribeTitle"));

        emailSubTextField = By.xpath(propUIPublicSite.getProperty("emailSubTextField"));
        pressReleaseBtn = By.xpath(propUIPublicSite.getProperty("pressReleaseBtn"));
        testListBtn = By.xpath(propUIPublicSite.getProperty("testListBtn"));
        eodBtn = By.xpath(propUIPublicSite.getProperty("eodBtn"));
        submitBtn = By.xpath(propUIPublicSite.getProperty("submitBtn"));
        errorMessage = By.xpath(propUIPublicSite.getProperty("errorMessage"));
        submitMessage =  By.xpath(propUIPublicSite.getProperty("submitMessage"));

        emailSubTextField_43 = By.xpath(propUIPublicSite.getProperty("emailSubTextField_4.3"));
        pressReleaseBtn_43 = By.xpath(propUIPublicSite.getProperty("pressReleaseBtn_4.3"));
        testListBtn_43 = By.xpath(propUIPublicSite.getProperty("testListBtn_4.3"));
        eodBtn_43 = By.xpath(propUIPublicSite.getProperty("eodBtn_4.3"));
        submitBtn_43 = By.xpath(propUIPublicSite.getProperty("submitBtn_4.3"));
        errorMessage_43 = By.xpath(propUIPublicSite.getProperty("errorMessage_4.3"));
        submitMessage_43 =  By.xpath(propUIPublicSite.getProperty("submitMessage_4.3"));

        emailUnsubTextField = By.xpath(propUIPublicSite.getProperty("emailUnsubTextField"));
        unsubscribeBtn = By.xpath(propUIPublicSite.getProperty("unsubscribeBtn"));
        unsubscribeMessage =By.xpath(propUIPublicSite.getProperty("unsubscribeMessage"));
        successMessage = By.xpath(propUIPublicSite.getProperty("successMessage"));

    }

    public String subscribe(JSONObject data, String type){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        if (type == "subscribe"){
            findElement(emailSubTextField).clear();
            findElement(emailSubTextField).click();
            findElement(emailSubTextField).sendKeys(data.get("email").toString());

            //Old subscribe has all the checkboxes already checked when first opened
            if (data.get("mailing_list").toString() == "true"){
                findElement(eodBtn).click();
                findElement(testListBtn).click();
                findElement(pressReleaseBtn).click();
            }
            findElement(submitBtn).click();
            System.out.println("Subscription was submitted at: " + dateFormat.format(new Date()));

            return getSysMessage(data, type).getText();
        } else if (type == "subscribe_43"){
            findElement(emailSubTextField_43).clear();
            findElement(emailSubTextField_43).click();
            findElement(emailSubTextField_43).sendKeys(data.get("email").toString());

            //New mailing lists do not have any of the lists checkboxed
            if (data.get("mailing_list").toString() == "false"){
                findElement(eodBtn_43).click();
                findElement(testListBtn_43).click();
                findElement(pressReleaseBtn_43).click();
            }
            findElement(submitBtn_43).click();
            System.out.println("Subscription was submitted at: " + dateFormat.format(new Date()));

            return getSysMessage(data, type).getText();
        }
        return null;
    }

    public String unsubscribe(JSONObject data, String type){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        findElement(emailUnsubTextField).clear();
        findElement(emailUnsubTextField).click();
        findElement(emailUnsubTextField).sendKeys(data.get("email").toString());
        findElement(unsubscribeBtn).click();
        System.out.println("Unsubscription was submitted at: " + dateFormat.format(new Date()));

        return getSysMessage(data, type).getText();
    }

    public boolean eventAlertPageDisplayed(){
        waitForElementToAppear(subscribeTitle);
        return true;
    }

    public boolean getEODChkBox(String type){
        if (type == "subscribe"){
            return findElement(eodBtn).isSelected();

        } else if (type == "subscribe_43"){
            return !findElement(eodBtn_43).isSelected();
        }
        return false;
    }
    
    public boolean getPressReleaseChkBox(String type){
        if (type == "subscribe"){
            return findElement(pressReleaseBtn).isSelected();
        } else if (type == "subscribe_43"){
            return !findElement(pressReleaseBtn_43).isSelected();
        }
        return false;
    }
    
    public boolean getTestListChkBox(String type){
        if (type == "subscribe"){
            return findElement(testListBtn).isSelected();
        } else if (type == "subscribe_43"){
            return !findElement(testListBtn_43).isSelected();
        }
        return false;
    }
    

    public WebElement getSysMessage(JSONObject data, String type) {
        WebElement element = null;

        try {
            switch(type){
                case "subscribe":
                    if(data.get("fail").toString() == "true"){
                        waitForElement(errorMessage);
                        return findElement(errorMessage);
                    } else{
                        waitForElement(submitMessage);
                        return findElement(submitMessage);
                    }
                case "subscribe_43":
                    if(data.get("fail").toString() == "true"){
                        waitForElement(errorMessage_43);
                        return findElement(errorMessage_43);
                    } else{
                        waitForElement(submitMessage_43);
                        return findElement(submitMessage_43);
                    }
                case "unsubscribe":
                        waitForElement(unsubscribeMessage);
                        return findElement(unsubscribeMessage);
                default:
                    System.out.println("Something is wrong");
            }
        } catch (ElementNotFoundException e1) {
        } catch (ElementNotVisibleException e2) {
        } catch (TimeoutException e3) {
        }

        return null;
    }

    public String checkAndGetEmail(String user, String password, String title) throws InterruptedException, IOException, MessagingException {
        //Change name to check email
        //Repeat if email is not found
        for(int i =1;i <=5; i++){
            Message email = getSpecificMail(user, password, title);
            if (email == null){
                System.out.println("Email was not found, attempt # "+ i);
            } else{
                System.out.println("Email found");
                System.out.println("Email was sent at: " + email.getSentDate());
                return StringUtils.substringBetween(email.getContent().toString(), "<a href=\"", "\">");
            }
        }
        return null;
    }

    public boolean checkMessage(String url, String type){
        driver.get(url);
        try{
            waitForElement(successMessage);
            String message = findElement(successMessage).getText();

            if (type == "unsubscribe"){
                if (message.equals("You were successfully unsubscribed.")){
                    return true;
                }
                return false;
            }
            if (type == "subscribe"){
                if(message.equals("Account successfully activated.")){
                    return true;
                }
                return false;
            }
            if (type == "subscribe_43"){
                if(message.equals("Account successfully activated.")){
                    return true;
                }
                return false;
            }
        } catch (ElementNotFoundException e1) {
        } catch (ElementNotVisibleException e2) {
        } catch (TimeoutException e3) {
        }
        return false;
    }
}
