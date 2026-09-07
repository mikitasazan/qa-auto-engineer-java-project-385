package hexlet.code.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import hexlet.code.config.Credentials;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.UsersPage;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UsersManagementTest extends SeleniumTestBase {
    @Test
    void createdUserAppearsInTheList() {
        var users = signedInUsersPage();
        var email = uniqueEmail();
        assertTrue(users.createUser(email, "John", "Doe"), "new user did not show up in the list");
    }

    @Test
    void userListShowsItsColumnsAndRows() {
        var seeded = seedUser();
        assertTrue(seeded.page.isListVisible(), "the Email column never appeared");
        assertTrue(
                seeded.page.isUserListed(seeded.email),
                "the seeded user is not visible in the table");
    }

    @Test
    void editingAUserUpdatesTheVisibleName() {
        var seeded = seedUser();
        assertTrue(seeded.page.renameUser(seeded.email, "Updated Name"));
    }

    @Test
    void deletingAUserRemovesItFromTheList() {
        var seeded = seedUser();
        assertTrue(seeded.page.removeUser(seeded.email));
    }

    @Test
    void deletingEveryUserLeavesTheEmptyState() {
        seedUser();
        var users = new UsersPage(driver, settings);
        assertTrue(users.removeEveryUser());
    }

    private UsersPage signedInUsersPage() {
        new LoginPage(driver, settings).signIn(Credentials.USERNAME, Credentials.PASSWORD);
        var users = new UsersPage(driver, settings);
        users.removeEveryUser();
        return users;
    }

    private SeededUser seedUser() {
        var users = signedInUsersPage();
        var email = uniqueEmail();
        assertTrue(users.createUser(email, "Name", "Surname"), "seed user was not created");
        return new SeededUser(users, email);
    }

    private String uniqueEmail() {
        return "user-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    private record SeededUser(UsersPage page, String email) {}
}
