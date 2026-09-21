package examples;

import org.openqa.selenium.By;
import org.testng.Assert;

/**
 * Reference implementation of a Page Object adhering to hybrid assertion strategy:
 * - Repetitive action-level verification (e.g., adding to cart successfully) is asserted INSIDE the function.
 * - Major scenario checkpoints (e.g., cart contents, order details) are NOT asserted here;
 *   instead, typed data/status is returned so the test case can assert them externally.
 * - Fluent returns (this on same page, new target page on navigation).
 */
public class SamplePage {

    // 1. Locators declared cleanly at top
    private static final By USERNAME_INPUT = By.id("username");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[name='login']");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector("button.add-to-cart");

    public SamplePage() {
    }

    public SamplePage enterUsername(String username) {
        return this;
    }

    public SamplePage enterPassword(String password) {
        return this;
    }

    public SampleDashboardPage submitLogin() {
        return new SampleDashboardPage();
    }

    /**
     * ACTION-LEVEL ASSERTION (Asserted inside function):
     * Repetitive action verification: Confirms that clicking "Add to cart" actually succeeded
     * (e.g., button class changed to 'added' or badge incremented) before proceeding.
     */
    public SamplePage addProductToCart(String productName) {
        // 1. Trigger action (e.g., element(ADD_TO_CART_BUTTON, "Add to cart").click())

        // 2. Assert directly in function for repeated action guarantee (fail-fast at step level)
        boolean isAddedSuccessfully = checkProductAddedSuccess(productName);
        Assert.assertTrue(isAddedSuccessfully,
                "Action verification failed: Product '" + productName + "' could not be added to cart.");
        return this;
    }

    private boolean checkProductAddedSuccess(String productName) {
        // Example: wait for button class 'added' or cart notification popup
        return true;
    }

    /**
     * MAJOR CHECKPOINT QUERY METHOD:
     * Returns state/typed data to let the test case assert major checkpoints.
     */
    public boolean isLoginFormDisplayed() {
        return true;
    }

    public int getCartItemCount() {
        return 1;
    }

    public static class SampleDashboardPage {
        public boolean isDisplayed() {
            return true;
        }
    }
}
