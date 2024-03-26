package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemDescriptionRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createNewRequestForItem(long userId, ItemDescriptionRequestDto request);

    List<ItemRequestDto> getItemRequestsForUser(long userId);

    List<ItemRequestDto> getItemRequestsFromOtherUsers(long userId, int from, int size);

    ItemRequestDto getItemRequestById(long userId, long requestId);
}
