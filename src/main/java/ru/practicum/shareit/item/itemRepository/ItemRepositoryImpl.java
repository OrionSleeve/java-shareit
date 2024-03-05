package ru.practicum.shareit.item.itemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private final Map<Long, Map<Long, Item>> userItemMap = new HashMap<>();
    private long id = 0L;

    @Override
    public Item createItem(long ownerId, Item item) {
        item.setId(++id);
        Map<Long, Item> userItems = userItemMap.getOrDefault(ownerId, new HashMap<>());
        userItems.put(item.getId(), item);
        itemMap.put(item.getId(), item);
        userItemMap.put(ownerId, userItems);
        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        if (itemMap.get(itemId) == null) {
            throw new NotFoundException("Item with ID " + itemId + " not found.");
        }
        return itemMap.get(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(long ownerId) {
        List<Item> itemsByOwner = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (item.getOwner().getId() == ownerId) {
                itemsByOwner.add(item);
            }
        }
        return itemsByOwner;
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public Item updateItemData(long itemId, long ownerId, Item item) {
        Item thisItem;
        thisItem = itemMap.get(itemId);
        if (thisItem == null) {
            throw new NotFoundException("Item with ID " + itemId + " not found.");
        }
        if (thisItem.getOwner().getId() != ownerId) {
            throw new NotFoundException("Item with ID " + itemId + " does not belong to owner with ID " + ownerId);
        }
        itemFieldsUpdate(thisItem, item);
        userItemMap.get(ownerId).put(itemId, thisItem);
        return thisItem;
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemMap.values().stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public void removeItem(long itemId) {
        Item item = getItemById(itemId);
        if (item == null) {
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

    private void itemFieldsUpdate(Item existingItems, Item newItems) {
        if (newItems.getName() != null
                && !newItems.getName().isBlank()
                && !Objects.equals(newItems.getName(), existingItems.getName())) {
            existingItems.setName(newItems.getName());
        }
        if (newItems.getDescription() != null
                && !newItems.getDescription().isBlank()
                && !Objects.equals(newItems.getDescription(), existingItems.getDescription())) {
            existingItems.setDescription(newItems.getDescription());
        }
        if (newItems.getAvailable() != null && !Objects.equals(newItems.getAvailable(), existingItems.getAvailable())) {
            existingItems.setAvailable(newItems.getAvailable());
        }
    }
}
