package homework1.service;

import homework1.dto.Item;

import java.util.List;

public interface XmlStatisticWriter {
    void write(List<Item> items);
}
