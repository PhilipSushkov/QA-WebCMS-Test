package specs.ContentAdmin.DepartmentList;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.Assert;

import org.openqa.selenium.By;
import pageobjects.ContentAdmin.DepartmentList.DepartmentList;
import pageobjects.Dashboard.Dashboard;
import pageobjects.LoginPage.LoginPage;
import specs.AbstractSpec;

/**
 * Created by philipsushkov on 2016-12-02.
 */

public class CheckDepartmentList extends AbstractSpec {
    private static By contentAdminMenuButton, departmentListMenuItem;
    private static LoginPage loginPage;
    private static Dashboard dashboard;
    private static DepartmentList departmentList;

    @BeforeTest
    public void setUp() throws Exception {
        contentAdminMenuButton = By.xpath(propUIContentAdmin.getProperty("btnMenu_ContentAdmin"));
        departmentListMenuItem = By.xpath(propUIContentAdmin.getProperty("btnMenu_DepartmentList"));

        loginPage = new LoginPage(driver);
        dashboard = new Dashboard(driver);
        departmentList = new DepartmentList(driver);

        loginPage.loginUser();
    }

    @Test
    public void checkDepartmentList() throws Exception {
        final String expectedTitle = "Department List";
        final Integer expectedQuantity = 2;

        dashboard.openPageFromMenu(contentAdminMenuButton, departmentListMenuItem);

        Assert.assertNotNull(departmentList.getUrl());
        Assert.assertEquals(departmentList.getTitle(), expectedTitle, "Actual Department List page Title doesn't match to expected");

        //System.out.println(new DepartmentList(driver).getTitleQuantity().toString());
        Assert.assertTrue(expectedQuantity <= departmentList.getTitleQuantity(), "Actual Department Name Quantity is less than expected: "+expectedQuantity);

    }

    @AfterTest
    public void tearDown() {
        dashboard.logoutFromAdmin();
        //driver.quit();
    }

}
