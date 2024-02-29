package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @NotNull @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item with owner ID {}", ownerId);
        return itemService.createItem(ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info("Fetching item by ID {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Fetching items by owner ID {}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long ownerId,
                              @RequestBody ItemDto itemDto) {
        log.info("Updating item with ID {} by owner ID {}", itemId, ownerId);
        return itemService.updateItemData(itemId, ownerId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Searching items with text: {}", text);
        return itemService.searchItems(text.toLowerCase());
    }

    @DeleteMapping
    public void removeItem(@PathVariable long itemId) {
        log.info("Removing item with ID {}", itemId);
        itemService.removeItem(itemId);
    }
}
