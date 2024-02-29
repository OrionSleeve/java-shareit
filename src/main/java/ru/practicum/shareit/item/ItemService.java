package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByOwner(long ownerId);

    List<ItemDto> getAllItems();

    ItemDto updateItemData(long itemId, long ownerId, ItemDto itemDto);

    List<ItemDto> searchItems(String text);

    void removeItem(long itemId);
}
