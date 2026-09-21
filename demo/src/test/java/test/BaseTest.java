package test;

import java.sql.Driver;
import driver.DriverManager;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    
    @BeforeMethod 
    public void setUp() {
        DriverManager.getDriver();
        // Any setup code can go here, if needed
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
        // Any cleanup code can go here, if needed
    }
}
