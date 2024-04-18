package homework1.service.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import homework1.dto.Item;
import homework1.exception.XmlWriterException;
import homework1.service.XmlStatisticWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class XmlStatWrImpl implements XmlStatisticWriter {

    public static final String STATISTICS = "statistics";
    private final Path resultPath;
    private final XmlMapper xmlMapper;

    public XmlStatWrImpl(Path resultPath) {
        this.resultPath = resultPath;
        this.xmlMapper = new XmlMapper();
    }
    @Override
    public void write(List<Item> items) {
        StandardOpenOption[] options = new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(resultPath, StandardCharsets.UTF_8, options)) {
            xmlMapper.writer().withRootName(STATISTICS).writeValue(bufferedWriter, items);
        } catch (IOException e) {
            throw new XmlWriterException("Unable to write items to XML file: " + resultPath);
        }
    }
}
