package hexlet.code.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import hexlet.code.config.Credentials;
import hexlet.code.pages.LabelsPage;
import hexlet.code.pages.LoginPage;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LabelsManagementTest extends SeleniumTestBase {
    @Test
    void createdLabelAppearsInTheList() {
        var labels = signedInLabelsPage();
        var name = "urgent-" + uniqueSuffix();
        assertTrue(labels.createLabel(name));
    }

    @Test
    void labelTableIsVisibleWithSeededRow() {
        var seeded = seedLabel();
        assertTrue(seeded.page.isTableVisible(), "labels table never appeared");
        assertTrue(
                seeded.page.isLabelListed(seeded.name), "seeded label is not visible in the table");
    }

    @Test
    void editingALabelUpdatesTheVisibleName() {
        var seeded = seedLabel();
        var updatedName = seeded.name + "-updated";
        assertTrue(seeded.page.renameLabel(seeded.name, updatedName));
    }

    @Test
    void deletingALabelRemovesItFromTheList() {
        var seeded = seedLabel();
        assertTrue(seeded.page.removeLabel(seeded.name));
    }

    @Test
    void deletingEveryLabelLeavesTheEmptyState() {
        seedLabel();
        var labels = new LabelsPage(driver, settings);
        assertTrue(labels.removeEveryLabel());
    }

    private LabelsPage signedInLabelsPage() {
        new LoginPage(driver, settings).signIn(Credentials.USERNAME, Credentials.PASSWORD);
        var labels = new LabelsPage(driver, settings);
        labels.removeEveryLabel();
        return labels;
    }

    private SeededLabel seedLabel() {
        var labels = signedInLabelsPage();
        var name = "label-" + uniqueSuffix();
        assertTrue(labels.createLabel(name), "seed label was not created");
        return new SeededLabel(labels, name);
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    private record SeededLabel(LabelsPage page, String name) {}
}
