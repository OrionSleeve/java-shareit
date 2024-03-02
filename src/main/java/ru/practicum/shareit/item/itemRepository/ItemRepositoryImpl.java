package ru.practicum.shareit.item.itemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.userService.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private final Map<Long, Map<Long, Item>> userItemMap = new HashMap<>();
    private long id = 0L;
    private final UserService userService;

    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.getUserById(ownerId)));
        item.setId(++id);
        Map<Long, Item> userItems = userItemMap.getOrDefault(ownerId, new HashMap<>());
        userItems.put(item.getId(), item);
        itemMap.put(item.getId(), item);
        userItemMap.put(ownerId, userItems);
        return ItemMapper.toItemDto(itemMap.get(item.getId()));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        if (itemMap.get(itemId) == null) {
            throw new NotFoundException("Item with ID " + itemId + " not found.");
        }
        return ItemMapper.toItemDto(itemMap.get(itemId));
    }

    @Override
    public List<ItemDto> getItemsByOwner(long ownerId) {
        Map<Long, Item> userItems = userItemMap.getOrDefault(ownerId, Collections.emptyMap());
        List<ItemDto> itemsByOwner = userItems
                .values()
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return itemsByOwner;
    }

    @Override
    public List<ItemDto> getAllItems() {
        if (itemMap.isEmpty()) {
            return Collections.emptyList();
        }
        return itemMap
                .values()
                .stream()
                .sorted(Comparator.comparing(Item::getName))
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItemData(long itemId, long ownerId, ItemDto itemDto) {
        Item thisItem;
        thisItem = itemMap.get(itemId);
        if (thisItem == null) {
            throw new NotFoundException("Item with ID " + itemId + " not found.");
        }
        if (thisItem.getOwner().getId() != ownerId) {
            throw new NotFoundException("Item with ID " + itemId + " does not belong to owner with ID " + ownerId);
        }
        itemFieldsUpdate(thisItem, itemDto);
        userItemMap.get(ownerId).put(itemId, thisItem);
        return ItemMapper.toItemDto(thisItem);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        String searchTextLowerCase = text.toLowerCase();

        return itemMap.values().stream().filter(Item::getAvailable).filter(item -> {
            String itemNameLowerCase = item.getName().toLowerCase();
            String itemDescriptionLowerCase = item.getDescription().toLowerCase();
            return itemNameLowerCase.contains(searchTextLowerCase)
                    || itemDescriptionLowerCase.contains(searchTextLowerCase);
        }).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public void removeItem(long itemId) {
        ItemDto itemDto = getItemById(itemId);
        if (itemDto == null) {
            throw new NotFoundException("Item with ID " + itemId + " not found.");
        }

        if (itemMap.remove(itemId) == null) {
            throw new NotFoundException("Item with ID " + itemId + " not found in itemMap.");
        }

        Map<Long, Item> userItems = userItemMap.get(itemId);
        if (userItems != null) {
            if (userItems.remove(itemId) == null) {
                throw new NotFoundException("Item with ID " + itemId + " not found in userItemMap.");
            }
        } else {
            throw new NotFoundException("User items not found for owner with ID " + itemId);
        }
    }

    private void itemFieldsUpdate(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null
                && !itemDto.getName().isBlank()
                && !Objects.equals(itemDto.getName(), item.getName())) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null
                && !itemDto.getDescription().isBlank()
                && !Objects.equals(itemDto.getDescription(), item.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !Objects.equals(itemDto.getAvailable(), item.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
    }
}
