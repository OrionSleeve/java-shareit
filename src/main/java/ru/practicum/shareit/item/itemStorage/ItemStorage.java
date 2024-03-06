package ru.practicum.shareit.item.itemStorage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(long ownerId, Item item);

    Item getItemById(long itemId);

    List<Item> getItemsByOwner(long ownerId);

    List<Item> getAllItems();

    Item updateItemData(long itemId, long ownerId, Item item);

    List<Item> searchItems(String text);

    void removeItem(long itemId);
}
