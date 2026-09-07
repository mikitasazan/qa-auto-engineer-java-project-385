package hexlet.code.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import hexlet.code.config.Credentials;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.StatusesPage;
import hexlet.code.pages.TasksPage;
import hexlet.code.pages.UsersPage;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class KanbanBoardTest extends SeleniumTestBase {
    @Test
    void creatingATaskShowsItsCardOnTheBoard() {
        var board = setUpBoard();
        var title = "Task-" + uniqueSuffix();
        assertTrue(board.tasks.createTask(title, board.content, board.assigneeEmail, board.status));
    }

    @Test
    void editingATaskTitleUpdatesTheCard() {
        var board = setUpBoard();
        var title = "Task-" + uniqueSuffix();
        var updatedTitle = title + "-renamed";

        assertTrue(board.tasks.createTask(title, board.content, board.assigneeEmail, board.status));
        assertTrue(board.tasks.editTask(title, updatedTitle, null, null));
        assertTrue(board.tasks.isCardVisible(updatedTitle));
    }

    @Test
    void movingATaskToAnotherStatusMovesTheCard() {
        var board = setUpBoard();
        var title = "Task-" + uniqueSuffix();

        assertTrue(board.tasks.createTask(title, board.content, board.assigneeEmail, board.status));
        assertTrue(board.tasks.editTask(title, null, null, board.alternateStatus));
        assertTrue(board.tasks.isCardInColumn(title, board.alternateStatus));
    }

    @Test
    void openingTaskDetailsShowsItsFullContent() {
        var board = setUpBoard();
        var title = "Task-" + uniqueSuffix();
        var content = "Details-" + board.content;

        assertTrue(board.tasks.createTask(title, content, board.assigneeEmail, board.status));

        board.tasks.openTaskDetails(title);
        assertTrue(board.tasks.isDetailVisible(board.assigneeEmail));
        assertTrue(board.tasks.isDetailVisible(title));
        assertTrue(board.tasks.isDetailVisible(content));
    }

    /** Signs in and seeds the two statuses and one user a board test needs. */
    private Board setUpBoard() {
        new LoginPage(driver, settings).signIn(Credentials.USERNAME, Credentials.PASSWORD);

        var statuses = new StatusesPage(driver, settings);
        statuses.removeEveryStatus();
        var suffix = uniqueSuffix();
        var primaryStatus = "Status-" + suffix;
        var alternateStatus = "Status-alt-" + suffix;
        assertTrue(statuses.createStatus(primaryStatus, "primary-" + suffix));
        assertTrue(statuses.createStatus(alternateStatus, "alternate-" + suffix));

        var users = new UsersPage(driver, settings);
        var assigneeEmail = "assignee-" + suffix + "@example.com";
        assertTrue(users.createUser(assigneeEmail, "Board", "Tester"));

        var tasks = new TasksPage(driver, settings);
        return new Board(
                tasks, assigneeEmail, primaryStatus, alternateStatus, "Seeded content " + suffix);
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    /**
     * Everything a board test needs after sign-in: no getters, this never leaves the test class.
     */
    private static final class Board {
        private final TasksPage tasks;
        private final String assigneeEmail;
        private final String status;
        private final String alternateStatus;
        private final String content;

        private Board(
                TasksPage tasks,
                String assigneeEmail,
                String status,
                String alternateStatus,
                String content) {
            this.tasks = tasks;
            this.assigneeEmail = assigneeEmail;
            this.status = status;
            this.alternateStatus = alternateStatus;
            this.content = content;
        }
    }
}
