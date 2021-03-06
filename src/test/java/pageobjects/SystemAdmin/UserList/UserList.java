package pageobjects.SystemAdmin.UserList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pageobjects.AbstractPageObject;

import java.util.List;

import static org.junit.Assert.fail;
import static specs.AbstractSpec.propUISystemAdmin;

/**
 * Created by philipsushkov on 2016-11-10.
 */

public class UserList extends AbstractPageObject {
    private static By moduleTitle, grid, gridUserName, addNewButton, editButton, userName, userActive;
    private final int columnsNumber = 4;

    public UserList(WebDriver driver) {
        super(driver);
        moduleTitle = By.xpath(propUISystemAdmin.getProperty("spanModule_Title"));
        grid = By.xpath(propUISystemAdmin.getProperty("table_GridUser"));
        gridUserName = By.xpath(propUISystemAdmin.getProperty("table_GridItem"));
        addNewButton = By.className(propUISystemAdmin.getProperty("btn_AddNewUser"));
        editButton = By.cssSelector(propUISystemAdmin.getProperty("btn_EditUser"));
        userName = By.cssSelector(propUISystemAdmin.getProperty("input_UserName"));
        userActive = By.cssSelector(propUISystemAdmin.getProperty("chk_UserActive"));
    }


    public String getTitle() {
        waitForElement(moduleTitle);
        return getText(moduleTitle);
    }

    public Integer getUserNameQuantity() {
        waitForElement(grid);
        return getGridRowQuantity(findElement(grid).findElements(gridUserName).size(), columnsNumber);
    }

    public UserEdit addNewUser(){
        waitForElement(addNewButton);
        findElement(addNewButton).click();
        return new UserEdit(getDriver());
    }

    // returns position (starting from 0) that username appears; returns -1 if username does not appear
    public int getIndexOfUsername(String desiredUsername){
        waitForElement(userName);
        List<WebElement> usernames = findElements(userName);
        for (int i=0; i<usernames.size(); i++){
            if (usernames.get(i).getText().equalsIgnoreCase(desiredUsername)){
                return i;
            }
        }
        return -1;
    }

    public boolean userIsActive(int index){
        waitForElement(userActive);
        if (findElements(userActive).get(index).getText().equalsIgnoreCase("true")){
            return true;
        }
        else if (findElements(userActive).get(index).getText().equalsIgnoreCase("false")){
            return false;
        }
        else {
            fail("Active status for index "+index+" displayed as invalid value "+findElements(userActive).get(index).getText());
            return false;
        }
    }

    public UserEdit editUser(int index){
        waitForElement(editButton);
        findElements(editButton).get(index).click();
        return new UserEdit(getDriver());
    }

}
