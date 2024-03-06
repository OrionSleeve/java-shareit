package ru.practicum.shareit.item.itemService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.itemStorage.ItemStorage;
import ru.practicum.shareit.item.itemStorage.ItemStorageImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userStorage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        User owner = checkUser(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = checkItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItemData(long itemId, long ownerId, ItemDto itemDto) {
        return null;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> foundItems = itemRepository.search(text);

        String searchTextLowerCase = text.toLowerCase();
        return foundItems.stream().filter(Item::getAvailable).filter(item -> {
            String itemNameLowerCase = item.getName().toLowerCase();
            String itemDescriptionLowerCase = item.getDescription().toLowerCase();
            return itemNameLowerCase.contains(searchTextLowerCase) || itemDescriptionLowerCase.contains(searchTextLowerCase);
        }).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public void removeItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    private User checkUser(long userId) {
       return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with userId: " + userId));
    }

    private Item checkItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found."));
    }

    /*
    private final ItemStorage itemRepository;
    private final UserStorage userRepository;

    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        User owner = userRepository.getUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.createItem(ownerId, item));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(long ownerId) {
        List<Item> items = itemRepository.getItemsByOwner(ownerId);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> items = itemRepository.getAllItems();
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItemData(long itemId, long ownerId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.updateItemData(itemId, ownerId, item));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> foundItems = itemRepository.searchItems(text);

        String searchTextLowerCase = text.toLowerCase();
        return foundItems.stream().filter(Item::getAvailable).filter(item -> {
            String itemNameLowerCase = item.getName().toLowerCase();
            String itemDescriptionLowerCase = item.getDescription().toLowerCase();
            return itemNameLowerCase.contains(searchTextLowerCase) || itemDescriptionLowerCase.contains(searchTextLowerCase);
        }).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public void removeItem(long itemId) {
        itemRepository.removeItem(itemId);
    }

     */
}
