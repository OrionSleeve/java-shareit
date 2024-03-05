package ru.practicum.shareit.item.itemService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.itemRepository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userRepository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

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
}
