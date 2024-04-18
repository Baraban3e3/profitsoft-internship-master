package homework1.service.impl;

import homework1.exception.JsonAttributeParseException;
import homework1.service.JsStatSer;
import homework1.service.XmlStatisticWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class JsStatSerImplTest {

    private static final String TEST_DATA_DIRECTORY = "src/test/resources";
    private static final String ATTRIBUTE_NAME = "subscription";
    private static final int TREAD_COUNT = 8;

    private static final String RESULT_FILE_EXTENSION = ".xml";
    private static final String RESULT_FILE_PREFIX = "statistics_by_";
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(TREAD_COUNT);
    }

    @Test
    void shouldFailOnWrongDirectory() {
        Path path = Path.of(TEST_DATA_DIRECTORY, RESULT_FILE_PREFIX + ATTRIBUTE_NAME + RESULT_FILE_EXTENSION);
        XmlStatisticWriter xmlWriter = new XmlStatWrImpl(path);
        JsStatSer statisticService = new JsStatImpl(executorService, xmlWriter);

        Assertions.assertThrows(JsonAttributeParseException.class,
                () -> statisticService.TakeAllStat("src/test/resources/in", ATTRIBUTE_NAME),
                "Cannot parse files in directory: src/test/resources/in"
        );
    }
}