package ru.practicum.shareit.item.itemRepository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(long ownerId, Item item);

    Item getItemById(long itemId);

    List<Item> getItemsByOwner(long ownerId);

    List<Item> getAllItems();

    Item updateItemData(long itemId, long ownerId, Item item);

    List<Item> searchItems(String text);

    void removeItem(long itemId);
}
