package test;

import driver.DriverManager;
import pages.ExamplePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExamplePageTest extends BaseTest {

    @Test
    public void headingDisplaysCorrectly() {

        DriverManager.getDriver()
                .get("https://example.com");

        ExamplePage page = new ExamplePage();

        String actualHeading = page.getHeadingText();

        Assert.assertEquals(
                actualHeading,
                "Example Domain"
        );
    }
}