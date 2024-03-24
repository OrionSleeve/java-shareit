package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.GroupsInterface;
import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.Constants.HEADER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HEADER) long ownerId,
                                             @RequestBody @Validated(GroupsInterface.Create.class) ItemDtoCreate itemDto) {
        log.info("Creating item with owner ID {}", ownerId);
        return itemClient.createItem(ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId,
                                              @RequestHeader(HEADER) long ownerId) {
        log.info("Fetching item by ID {}", itemId);
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemByOwner(@RequestHeader(HEADER) long ownerId,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        log.info("Fetching items by owner ID {}", ownerId);
        return itemClient.getItemsByOwner(ownerId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId,
                                             @RequestHeader(HEADER) long ownerId,
                                             @Validated(GroupsInterface.Update.class) @RequestBody ItemDtoCreate itemDto) {
        log.info("Updating item with ID {} by owner ID {}", itemId, ownerId);
        return itemClient.updateItemData(itemId, ownerId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                              @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        log.info("Searching items with text: {}", text);
        return itemClient.searchItems(text.toLowerCase(), from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> removeItem(@PathVariable long itemId) {
        log.info("Removing item with ID {}", itemId);
        return itemClient.removeItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(
            @PathVariable long itemId,
            @RequestHeader(HEADER) long bookerId,
            @Valid @RequestBody CommentReqDto commentRequestDto) {
        log.info("Received request to create comment for item with ID {} from booker with ID {}", itemId, bookerId);
        return itemClient.addComment(itemId, bookerId, commentRequestDto);
    }
}
