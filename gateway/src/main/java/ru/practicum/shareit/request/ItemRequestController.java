package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemDescriptionRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.Constants.HEADER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(HEADER) long userId,
            @RequestBody @Valid ItemDescriptionRequestDto request) {
        log.info("Creating new item request for user {}", userId);
        return itemRequestClient.createNewRequestForItem(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader(HEADER) long userId) {
        log.info("Getting item requests for user {}", userId);
        return itemRequestClient.getItemRequestsForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersItemRequests(
            @RequestHeader(HEADER) long userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        log.info("Getting item requests from other users for user {}", userId);
        return itemRequestClient.getItemRequestsFromOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader(HEADER) long userId,
            @PathVariable(name = "requestId") long requestId) {
        log.info("Getting item request by id {} for user {}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
