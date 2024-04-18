package homework1;

import homework1.exception.JsonAttributeParseException;
import homework1.service.XmlStatisticWriter;
import homework1.service.impl.JsStatImpl;
import homework1.service.impl.XmlStatWrImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testTakeAllStatWithValidDirectoryAndAttribute() {
        // Arrange
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        XmlStatisticWriter xmlWriter = new XmlStatWrImpl(Path.of("test.xml"));
        JsStatImpl statisticService = new JsStatImpl(executorService, xmlWriter);

        // Act
        statisticService.TakeAllStat("src/test/resources", "subscription");

        // Assert - Check if the output XML file is created successfully
        // Here you can add assertions to check the content of the XML file if needed
        // For simplicity, we just check if the file exists
        assertTrue(Files.exists(Path.of("test.xml")));

        // Clean up
        executorService.shutdown();
    }

    @Test
    void testTakeAllStatWithInvalidDirectory() {
        // Arrange
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        XmlStatisticWriter xmlWriter = new XmlStatWrImpl(Path.of("test.xml"));
        JsStatImpl statisticService = new JsStatImpl(executorService, xmlWriter);

        // Act & Assert
        assertThrows(JsonAttributeParseException.class, () -> statisticService.TakeAllStat("invalid-directory", "subscription"));

        // Clean up
        executorService.shutdown();
    }

    @Test
    void testTakeAllStatWithNonexistentDirectory() {
        // Arrange
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        XmlStatisticWriter xmlWriter = new XmlStatWrImpl(Path.of("test.xml"));
        JsStatImpl statisticService = new JsStatImpl(executorService, xmlWriter);

        // Act & Assert
        assertThrows(JsonAttributeParseException.class, () -> statisticService.TakeAllStat("nonexistent-directory", "subscription"));

        // Clean up
        executorService.shutdown();
    }

}