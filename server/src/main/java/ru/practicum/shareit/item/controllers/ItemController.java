package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.CommentResDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.itemService.ItemService;

import java.util.List;

import static ru.practicum.shareit.Constants.HEADER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER) long ownerId,
                              @RequestBody ItemDtoCreate itemDto) {
        log.info("Creating item with owner ID {}", ownerId);
        return itemService.createItem(ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader(HEADER) long ownerId) {
        log.info("Fetching item by ID {}", itemId);
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemByOwner(@RequestHeader(HEADER) long ownerId,
                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Fetching items by owner ID {}", ownerId);
        return itemService.getItemsByOwner(ownerId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestHeader(HEADER) long ownerId,
                              @RequestBody ItemDtoCreate itemDto) {
        log.info("Updating item with ID {} by owner ID {}", itemId, ownerId);
        return itemService.updateItemData(itemId, ownerId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Searching items with text: {}", text);
        return itemService.searchItems(text.toLowerCase(), from, size);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@PathVariable long itemId) {
        log.info("Removing item with ID {}", itemId);
        itemService.removeItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResDto addCommentToItem(
            @PathVariable long itemId,
            @RequestHeader(HEADER) long bookerId,
            @RequestBody CommentReqDto commentRequestDto) {
        log.info("Received request to create comment for item with ID {} from booker with ID {}", itemId, bookerId);
        return itemService.addComment(commentRequestDto, bookerId, itemId);
    }
}
