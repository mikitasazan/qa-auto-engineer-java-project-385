package hexlet.code.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import hexlet.code.config.Credentials;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.StatusesPage;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StatusesManagementTest extends SeleniumTestBase {
    @Test
    void createdStatusAppearsInTheList() {
        var statuses = signedInStatusesPage();
        var suffix = uniqueSuffix();
        assertTrue(statuses.createStatus("Backlog " + suffix, "backlog-" + suffix));
    }

    @Test
    void statusListShowsItsColumns() {
        var statuses = signedInStatusesPage();
        assertTrue(statuses.isListVisible(), "the Name/Slug columns never appeared");
    }

    @Test
    void editingAStatusUpdatesTheVisibleName() {
        var seeded = seedStatus();
        var updatedName = seeded.name + " Updated";
        assertTrue(seeded.page.renameStatus(seeded.name, updatedName));
    }

    @Test
    void deletingAStatusRemovesItFromTheList() {
        var seeded = seedStatus();
        assertTrue(seeded.page.removeStatus(seeded.name));
    }

    @Test
    void deletingEveryStatusLeavesTheEmptyState() {
        seedStatus();
        var statuses = new StatusesPage(driver, settings);
        assertTrue(statuses.removeEveryStatus());
    }

    private StatusesPage signedInStatusesPage() {
        new LoginPage(driver, settings).signIn(Credentials.USERNAME, Credentials.PASSWORD);
        var statuses = new StatusesPage(driver, settings);
        statuses.removeEveryStatus();
        return statuses;
    }

    private SeededStatus seedStatus() {
        var statuses = signedInStatusesPage();
        var suffix = uniqueSuffix();
        var name = "Status " + suffix;
        assertTrue(statuses.createStatus(name, "slug-" + suffix), "seed status was not created");
        return new SeededStatus(statuses, name);
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    private record SeededStatus(StatusesPage page, String name) {}
}
