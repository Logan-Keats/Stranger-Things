package com.strangerthings;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

class FxmlLoadTest {

    @BeforeAll
    static void startToolkit() {
        Platform.startup(() -> { });
        Platform.setImplicitExit(false);
    }

    @AfterAll
    static void stopToolkit() {
        Platform.exit();
    }

    @Test
    void sectionSixViewsLoad() throws InterruptedException {
        assertFxmlLoads("/com/strangerthings/login.fxml");
        assertFxmlLoads("/com/strangerthings/register.fxml");
        assertFxmlLoads("/com/strangerthings/home.fxml");
        assertFxmlLoads("/com/strangerthings/reports.fxml");
        assertFxmlLoads("/com/strangerthings/main-shell.fxml");
    }

    @Test
    void shellLoadsHomeAndReportsForAdmin() throws InterruptedException {
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch completed = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(FxmlLoadTest.class.getResource("/com/strangerthings/main-shell.fxml"));
                Parent root = loader.load();
                MainShellController controller = loader.getController();
                Button reportsButton = (Button) root.lookup("#reportsButton");
                Button allBookingsButton = (Button) root.lookup("#allBookingsButton");
                Label userNameLabel = (Label) root.lookup("#userNameLabel");
                StackPane contentArea = (StackPane) root.lookup("#contentArea");

                controller.setLoggedInUser("admin");
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                try {
                    assertTrue(reportsButton.isVisible());
                    assertTrue(allBookingsButton.isVisible());
                    assertTrue(userNameLabel.getText().contains("admin"));
                    assertFalse(contentArea.getChildren().isEmpty());
                    assertEquals("javafx.scene.control.ScrollPane",
                            contentArea.getChildren().getFirst().getClass().getName());
                    Node homeView = contentArea.getChildren().getFirst();

                    reportsButton.fire();
                    assertEquals("javafx.scene.control.ScrollPane",
                            contentArea.getChildren().getFirst().getClass().getName());
                    assertNotSame(homeView, contentArea.getChildren().getFirst());
                } catch (RuntimeException | AssertionError exception) {
                    error.set(exception);
                } finally {
                    stage.hide();
                    completed.countDown();
                }
            } catch (IOException | RuntimeException | AssertionError exception) {
                error.set(exception);
                completed.countDown();
            }
        });

        if (!completed.await(10, TimeUnit.SECONDS)) {
            fail("Timed out checking role-based Home navigation");
        }
        assertNull(error.get(), () -> "Role navigation failed: " + error.get());
    }

    private void assertFxmlLoads(String resource) throws InterruptedException {
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch completed = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                FXMLLoader.load(FxmlLoadTest.class.getResource(resource));
            } catch (IOException | RuntimeException exception) {
                error.set(exception);
            } finally {
                completed.countDown();
            }
        });

        if (!completed.await(10, TimeUnit.SECONDS)) {
            fail("Timed out loading " + resource);
        }
        assertNull(error.get(), () -> "Could not load " + resource + ": " + error.get());
    }
}
