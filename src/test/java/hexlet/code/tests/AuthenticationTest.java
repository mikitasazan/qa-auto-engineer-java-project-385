package hexlet.code.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import hexlet.code.config.Credentials;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.UsersPage;
import org.junit.jupiter.api.Test;

class AuthenticationTest extends SeleniumTestBase {
    @Test
    void signInReachesTheDashboard() {
        var loginPage = new LoginPage(driver, settings);
        loginPage.signIn(Credentials.USERNAME, Credentials.PASSWORD);
        assertTrue(loginPage.isOnDashboard(), "dashboard content did not appear after sign in");

        // A session flag alone proves nothing: the same check must survive an
        // actual navigation to a protected route, not just the one screen the
        // login click happened to leave behind.
        assertTrue(
                new UsersPage(driver, settings).isListVisible(),
                "signed-in session could not open a protected page (Users)");
    }

    @Test
    void signOutReturnsToTheSignInScreen() {
        var loginPage = new LoginPage(driver, settings);
        loginPage.signIn(Credentials.USERNAME, Credentials.PASSWORD);
        loginPage.signOut();
        assertTrue(loginPage.isOnSignInScreen(), "sign-in screen did not reappear after logout");
    }
}
