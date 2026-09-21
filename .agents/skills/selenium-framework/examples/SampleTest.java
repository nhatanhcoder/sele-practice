package examples;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Reference implementation of a TestNG test adhering to the hybrid assertion strategy:
 *
 * 1. ACTION-LEVEL (In-function assertions):
 *    - Handled inside Page Object methods (e.g., samplePage.addProductToCart(...) internally
 *      asserts that the item was successfully added to cart before returning).
 *
 * 2. MAJOR CHECKPOINTS (In Test Case):
 *    - All high-level scenario verifications (cart items, order details, confirmation messages)
 *      MUST be asserted here in the @Test method for clarity and traceability.
 */
public class SampleTest {

    @Test
    public void testPurchaseScenario() {
        SoftAssert softAssert = new SoftAssert();

        SamplePage samplePage = new SamplePage();

        // [Precondition]: Hard assert to fail-fast if page not ready
        Assert.assertTrue(samplePage.isLoginFormDisplayed(),
                "Precondition failed: Login form must be displayed.");

        // [Action with in-function assert]:
        // Inside `addProductToCart`, it already asserts that the add-to-cart action succeeded!
        samplePage.addProductToCart("Arduino Uno R3");

        // [MAJOR CHECKPOINT 1 of Test Case]: Must be asserted OUTSIDE in the test case
        softAssert.assertEquals(samplePage.getCartItemCount(), 1,
                "Major Checkpoint: Cart item count should match the number of added products.");

        // [Navigate]:
        SamplePage.SampleDashboardPage dashboardPage = samplePage
                .enterUsername("testuser")
                .enterPassword("password123")
                .submitLogin();

        // [MAJOR CHECKPOINT 2 of Test Case]: Must be asserted OUTSIDE in the test case
        softAssert.assertTrue(dashboardPage.isDisplayed(),
                "Major Checkpoint: User dashboard must be visible after login.");

        // Flush all soft assertions at the boundary of the test
        softAssert.assertAll();
    }
}
