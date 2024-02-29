package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        return itemRepository.createItem(ownerId, itemDto);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getItemsByOwner(long ownerId) {
        return itemRepository.getItemsByOwner(ownerId);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemRepository.getAllItems();
    }

    @Override
    public ItemDto updateItemData(long itemId, long ownerId, ItemDto itemDto) {
        return itemRepository.updateItemData(itemId, ownerId, itemDto);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text);
    }

    @Override
    public void removeItem(long itemId) {
        itemRepository.removeItem(itemId);
    }
}
