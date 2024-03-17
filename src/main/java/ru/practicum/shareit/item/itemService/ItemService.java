package ru.practicum.shareit.item.itemService;

import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentResDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto getItemById(long itemId, long ownerId);

    List<ItemDto> getItemsByOwner(long ownerId, int from, int size);

    ItemDto updateItemData(long itemId, long ownerId, ItemDto itemDto);

    List<ItemDto> searchItems(String text, int from, int size);

    void removeItem(long itemId);

    CommentResDto addComment(CommentReqDto commentRequestDto, long bookerId, long itemId);
}
