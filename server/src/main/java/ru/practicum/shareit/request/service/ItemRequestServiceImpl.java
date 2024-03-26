package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemDescriptionRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequestDto createNewRequestForItem(long userId, ItemDescriptionRequestDto request) {
        return userRepository.findById(userId)
                .map(user -> {
                    ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(request);
                    itemRequest.setRequester(user);
                    itemRequest.setCreated(LocalDateTime.now());
                    itemRequest = itemRequestRepository.save(itemRequest);
                    return ItemRequestMapper.toRequestWithItemsDto(itemRequest);
                })
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " doesn't exist."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestsForUser(long userId) {
        checkUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(userId);

        Map<Long, List<ItemForRequestDto>> itemsMap = getItemResponsesForRequests(itemRequests);

        return itemRequests.stream()
                .map(ItemRequestMapper::toRequestWithItemsDto)
                .peek(r -> r.setItems(itemsMap.getOrDefault(r.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestsFromOtherUsers(long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequesterIdIsNot(userId, page);

        Map<Long, List<ItemForRequestDto>> itemsMap = getItemResponsesForRequests(itemRequests);

        return itemRequests.stream()
                .map(ItemRequestMapper::toRequestWithItemsDto)
                .peek(r -> r.setItems(itemsMap.getOrDefault(r.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestDto getItemRequestById(long userId, long requestId) {
        checkUser(userId);
        checkItemRequest(requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request not found " + requestId));
        ItemRequestDto response = ItemRequestMapper.toRequestWithItemsDto(itemRequest);
        List<ItemForRequestDto> items = getItemResponsesForRequest(requestId);
        response.setItems(items);

        return response;
    }

    private List<ItemForRequestDto> getItemResponsesForRequest(long requestId) {
        List<ItemForRequestDto> items = itemRepository.getItemDescriptionForRequest(requestId);
        if (!items.isEmpty()) {
            return items;
        }
        return Collections.emptyList();
    }

    private void checkUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with userId: " + userId));
    }

    private void checkItemRequest(long requestId) {
        userRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request not found" + requestId));
    }

    private Map<Long, List<ItemForRequestDto>> getItemResponsesForRequests(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toMap(Function.identity(), this::getItemResponsesForRequest));
    }
}
