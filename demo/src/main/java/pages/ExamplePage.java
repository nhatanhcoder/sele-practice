package pages;

import org.openqa.selenium.By;


public class ExamplePage extends BasePage {

    private final By Heading = By.xpath("//h1");

    public String getHeadingText() {
        return getText(Heading);
    }
    
}
