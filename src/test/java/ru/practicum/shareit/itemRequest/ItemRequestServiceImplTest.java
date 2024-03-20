package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemDescriptionRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    void createNewItemRequest_whenValid_thenReturnRequest() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemDescriptionRequestDto itemRequestDescription = new ItemDescriptionRequestDto();
        itemRequestDescription.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription.getDescription());
        itemRequest.setRequester(user);
        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        itemRequest.setCreated(created);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto expected = ItemRequestMapper.toRequestWithItemsDto(itemRequest);
        ItemRequestDto actual = itemRequestService.createNewRequestForItem(userId, itemRequestDescription);
        assertEquals(expected, actual);
    }

    @Test
    void createNewItemRequest_whenUserNotFound_thenUserNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.createNewRequestForItem(
                        1L,
                        new ItemDescriptionRequestDto()
                )
        );
    }

    @Test
    void getUserItemRequests_whenValid_thenReturnItemRequestWithItems() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemDescriptionRequestDto itemRequestDescription = new ItemDescriptionRequestDto();
        itemRequestDescription.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.getItemDescriptionForRequest(itemRequestId)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> expected =
                Stream.of(itemRequest)
                        .map(ItemRequestMapper::toRequestWithItemsDto)
                        .collect(Collectors.toList());
        for (ItemRequestDto r : expected) {
            List<ItemForRequestDto> items = itemRepository.getItemDescriptionForRequest(r.getId());
            r.setItems(items);
        }
        List<ItemRequestDto> actual = itemRequestService.getItemRequestsForUser(userId);
        assertEquals(expected, actual);
    }

    @Test
    void getUserItemRequests_whenUserNotFound_thenUserNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsForUser(userId));
    }


    @Test
    void getItemRequestById_whenValid_thenReturnItemRequest() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemDescriptionRequestDto itemRequestDescription = new ItemDescriptionRequestDto();
        itemRequestDescription.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.getItemDescriptionForRequest(itemRequestId)).thenReturn(Collections.emptyList());

        ItemRequestDto expected = ItemRequestMapper.toRequestWithItemsDto(itemRequest);
        expected.setItems(Collections.emptyList());
        ItemRequestDto actual = itemRequestService.getItemRequestById(userId, itemRequestId);
        assertEquals(expected, actual);
    }

    @Test
    void getItemRequestById_whenValid_thenReturnItemRequestWithItemsList() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemDescriptionRequestDto itemRequestDescription = new ItemDescriptionRequestDto();
        itemRequestDescription.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(user);
        item.setId(itemId);
        item.setAvailable(true);
        item.setDescription("description");
        item.setRequest(itemRequest);

        ItemForRequestDto itemResponseForRequest = new ItemForRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId()
        );


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.getItemDescriptionForRequest(itemRequestId)).thenReturn(List.of(itemResponseForRequest));

        ItemRequestDto expected = ItemRequestMapper.toRequestWithItemsDto(itemRequest);
        expected.setItems(List.of(itemResponseForRequest));
        ItemRequestDto actual = itemRequestService.getItemRequestById(userId, itemRequestId);
        assertEquals(expected, actual);
    }

    @Test
    void getItemRequestById_whenUserNotFound_thenUserNotFoundException() {
        long userId = 1L, itemRequestId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(
                        userId,
                        itemRequestId
                )
        );
    }

    @Test
    void getItemRequestById_whenItemRequestNotFound_thenItemRequestNotFoundException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);
        long itemRequestId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(
                        userId,
                        itemRequestId
                )
        );
    }
}
