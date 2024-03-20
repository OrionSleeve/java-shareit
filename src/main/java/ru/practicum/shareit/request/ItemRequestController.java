package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemDescriptionRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.Constants.HEADER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestHeader(HEADER) long userId,
            @RequestBody @Valid ItemDescriptionRequestDto request) {
        log.info("Creating new item request for user {}", userId);
        return itemRequestService.createNewRequestForItem(userId, request);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader(HEADER) long userId) {
        log.info("Getting item requests for user {}", userId);
        return itemRequestService.getItemRequestsForUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersItemRequests(
            @RequestHeader(HEADER) long userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        log.info("Getting item requests from other users for user {}", userId);
        return itemRequestService.getItemRequestsFromOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(
            @RequestHeader(HEADER) long userId,
            @PathVariable(name = "requestId") long requestId) {
        log.info("Getting item request by id {} for user {}", requestId, userId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
