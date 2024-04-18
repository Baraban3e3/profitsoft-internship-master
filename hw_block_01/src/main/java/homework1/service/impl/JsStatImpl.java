package homework1.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import homework1.dto.Item;
import homework1.exception.JsonAttributeParseException;
import homework1.service.JsStatSer;
import homework1.service.XmlStatisticWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.StreamSupport;

public class JsStatImpl implements JsStatSer {


    private static final String COMMA = ",";
    private static final String JSON_EXTENSION = "*.json";
    private final JsonFactory jsonFactory;
    private final ExecutorService executorService;
    private final XmlStatisticWriter statisticWriter;

    public JsStatImpl(ExecutorService executorService, XmlStatisticWriter statisticWriter) {
        this.executorService = executorService;
        this.statisticWriter = statisticWriter;
        this.jsonFactory = new JsonFactory();
    }

    public void TakeAllStat(String directoryPath, String attribute) {
        Path path = Paths.get(directoryPath);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, "*" + JSON_EXTENSION)) {
            List<CompletableFuture<Map<String, Long>>> parseFileTasks = StreamSupport.stream(directoryStream.spliterator(), false)
                    .map(currentFileName -> parsing(attribute, currentFileName))
                    .toList();

            CompletableFuture.allOf(parseFileTasks.toArray(new CompletableFuture[0]))
                    .thenApplyAsync(future -> parseFileTasks.stream()
                            .map(CompletableFuture::join)
                            .toList())
                    .thenAcceptAsync(itemMap -> {
                        List<Item> groupedItems = AllItemsTogether(itemMap);
                        statisticWriter.write(groupedItems);
                        executorService.shutdown();
                    })
                    .join();
        } catch (IOException e) {
            throw new JsonAttributeParseException("Cannot parse files in directory: " + directoryPath);
        }
    }





    private List<Item> AllItemsTogether(List<Map<String, Long>> mapStream) {
        Map<String, Long> mergedMap = new HashMap<>();

        mapStream.forEach(map -> map.forEach((key, value) -> mergedMap.merge(key, value, Long::sum)));

        List<Item> itemList = new ArrayList<>();
        mergedMap.forEach((key, value) -> itemList.add(new Item(key, value)));

        return itemList.stream()
                .sorted(Comparator.comparing(Item::count).reversed())
                .toList();
    }

    private CompletableFuture<Map<String, Long>> parsing(String attribute, Path currentFileName) {
        return CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = Files.newBufferedReader(currentFileName); JsonParser jsonParser = jsonFactory.createParser(reader)) {
                return parseAttribute(attribute, jsonParser);
            } catch (JsonAttributeParseException | IOException e) {
                throw new JsonAttributeParseException("Cannot parse file: " + currentFileName.getFileName());
            }
        }, executorService);
    }

    private Map<String, Long> parseAttribute(String attribute, JsonParser jsonParser) {
        Map<String, Long> attributeMap = new HashMap<>();
        try {
            if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                throw new JsonAttributeParseException("Expected json content to be an array");
            }

            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                if (attribute.equals(jsonParser.currentName())) {
                    jsonParser.nextToken();
                    String attributeValue = jsonParser.getText();
                    String[] splitAttributes = attributeValue.split(COMMA);
                    for (String attr : splitAttributes) {
                        attributeMap.merge(attr.trim(), 1L, Long::sum);
                    }
                }
            }
        } catch (IOException e) {
            throw new JsonAttributeParseException("Error while parsing json content");
        }
        return attributeMap;
    }
}
