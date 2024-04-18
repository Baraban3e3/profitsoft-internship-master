package homework1;

import homework1.service.JsStatSer;
import homework1.service.XmlStatisticWriter;
import homework1.service.impl.JsStatImpl;
import homework1.service.impl.XmlStatWrImpl;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    private static final String RESULT_FILE_EXTENSION = ".xml";
    private static final String RESULT_FILE_PREFIX = "statistics_by_";
    private static final int DEFAULT_THREAD_COUNT = 8;

    public void runTask(String directoryPath, String attribute, int threadCount) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Path path = Path.of(directoryPath, RESULT_FILE_PREFIX + attribute + RESULT_FILE_EXTENSION);
        XmlStatisticWriter xmlWriter = new XmlStatWrImpl(path);
        JsStatSer statisticService = new JsStatImpl(executorService, xmlWriter);
        statisticService.TakeAllStat(directoryPath, attribute);
    }

    public static void main(String[] args) {
        String directoryPath = args[0];
        String attributeName = args[1];

        if (StringUtils.isBlank(directoryPath)) {
            log.severe("Directory path cannot be empty or blank");
        } else if (StringUtils.isBlank(attributeName)) {
            log.severe("Attribute name cannot be empty or blank");
        } else {
            Main main = new Main();
            main.runTask(directoryPath, attributeName, DEFAULT_THREAD_COUNT);
        }
    }
}
