package ru.practicum.shareit.item.itemService;

import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentResDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long ownerId, ItemDtoCreate itemDto);

    ItemDto getItemById(long itemId, long ownerId);

    List<ItemDto> getItemsByOwner(long ownerId, Integer from, Integer size);

    ItemDto updateItemData(long itemId, long ownerId, ItemDtoCreate itemDto);

    List<ItemDto> searchItems(String text, int from, int size);

    void removeItem(long itemId);

    CommentResDto addComment(CommentReqDto commentRequestDto, long bookerId, long itemId);
}
