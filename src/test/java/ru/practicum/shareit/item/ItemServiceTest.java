package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.bookingRepository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.itemService.ItemServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    public ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void createItem_whenUserIdInvalid_thenUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, ItemDtoCreate.builder().build()));
    }

    @Test
    void createItem_whenUserIdCorrect_thenCreateItem() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemDtoCreate itemDto = ItemDtoCreate.builder().build();
        Item item = new Item();
        long itemId = 0L;
        item.setOwner(user);
        item.setId(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto expectItemDto = ItemMapper.toItemDto(item);
        ItemDto actualItemDto = itemService.createItem(userId, itemDto);
        assertEquals(expectItemDto, actualItemDto);
    }

    @Test
    void createItem_whenRequestIdIsPresent_thenCreateItemWithRequest() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemDtoCreate itemDto = ItemDtoCreate.builder().build();
        Item item = new Item();
        long itemId = 0L;
        item.setId(itemId);
        item.setOwner(user);

        ItemRequest request = new ItemRequest();
        long requestId = 1L;
        request.setId(requestId);
        item.setRequest(request);
        itemDto.setRequestId(requestId);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto expectItemDto = ItemMapper.toItemDto(item);
        ItemDto actualItemDto = itemService.createItem(userId, itemDto);
        assertEquals(expectItemDto, actualItemDto);
    }

    @Test
    void createItem_whenRequestIdInvalid_thenItemRequestNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, ItemDtoCreate.builder().requestId(1L).build()));
    }

    @Test
    void getItemById_whenUserIdAndItemIdValid_thenReturnItem() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        List<BookingDto> nextBookingClosest = List.of(new BookingDto());
        List<BookingDto> lastBookingClosest = List.of(new BookingDto());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());
        when(bookingRepository.findNextClosestBookingByOwnerId(userId, itemId)).thenReturn(nextBookingClosest);
        when(bookingRepository.findLastClosestBookingByOwnerId(userId, itemId)).thenReturn(lastBookingClosest);

        ItemDto actualItemDto = ItemMapper.toItemDto(item);
        actualItemDto.setComments(Collections.emptyList());
        actualItemDto.setNextBooking(nextBookingClosest.get(0));
        actualItemDto.setLastBooking(lastBookingClosest.get(0));
        ItemDto expectItemDto = itemService.getItemById(itemId, userId);
        assertEquals(actualItemDto, expectItemDto);
    }

    @Test
    void getItemsByOwnerId_whenUserIdAndItemIdValid_thenReturnItemsList() {
        int from = 0, size = 10;
        Pageable page = PageRequest.of(from / size, size);

        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        List<BookingDto> nextBookingClosest = List.of(new BookingDto());
        List<BookingDto> lastBookingClosest = List.of(new BookingDto());

        when(itemRepository.findAllByOwnerId(userId, page)).thenReturn(List.of(item));
        when(bookingRepository.findNextClosestBookingByOwnerId(userId, itemId)).thenReturn(nextBookingClosest);
        when(bookingRepository.findLastClosestBookingByOwnerId(userId, itemId)).thenReturn(lastBookingClosest);

        List<ItemDto> expectItemDto = List.of(ItemMapper.toItemDto(item));
        expectItemDto.get(0).setComments(Collections.emptyList());
        expectItemDto.get(0).setNextBooking(nextBookingClosest.get(0));
        expectItemDto.get(0).setLastBooking(lastBookingClosest.get(0));
        List<ItemDto> actualItemDto = itemService.getItemsByOwner(userId, from, size);
        assertEquals(expectItemDto, actualItemDto);
    }

    /*
    @Test
    void updateItem_whenValid_thenReturnUpdatedItem() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item oldItem = new Item();
        long itemId = 1L;
        oldItem.setId(itemId);
        oldItem.setOwner(user);

        Item newItem = new Item();
        newItem.setId(itemId);
        newItem.setOwner(user);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(newItem));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());

        ItemDto expectItem = ItemMapper.toItemDto(newItem);
        expectItem.setComments(Collections.emptyList());
        ItemDto actualItem = itemService.updateItemData(itemId, userId, ItemMapper.toItem(newItem));
        assertEquals(expectItem, actualItem);
    }

     */

    @Test
    void searchItems_whenValid_thenReturnItemList() {
        String text1 = "";
        String text2 = "Name1";
        String text3 = "Description";

        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item1 = new Item();
        long itemId1 = 1L;
        item1.setId(itemId1);
        item1.setOwner(user);

        Item item2 = new Item();
        long itemId2 = 2L;
        item2.setId(itemId2);
        item2.setOwner(user);

        int from = 0, size = 10;
        Pageable page = PageRequest.of(from / size, size);

        when(itemRepository.searchItemByNameOrDescription(text2, page)).thenReturn(List.of(item1, item2));
        when(itemRepository.searchItemByNameOrDescription(text3, page)).thenReturn(List.of(item1, item2));

        List<ItemDto> expectedList1 = Collections.emptyList();
        assertEquals(expectedList1, itemService.searchItems(text1, from, size));

        List<ItemDto> expectedList2 = ItemMapper.toItemDto(List.of(item1, item2));
        expectedList2.get(0).setComments(Collections.emptyList());
        expectedList2.get(1).setComments(Collections.emptyList());
        assertEquals(expectedList2, itemService.searchItems(text2, from, size));

        List<ItemDto> expectedList3 = ItemMapper.toItemDto(List.of(item1, item2));
        expectedList3.get(0).setComments(Collections.emptyList());
        expectedList3.get(1).setComments(Collections.emptyList());
        assertEquals(expectedList3, itemService.searchItems(text3, from, size));
    }

    @Test
    void deleteItem_whenExecuted_thenItemIsRemovedFromDb() {
        long itemId = 1L;
        itemService.removeItem(itemId);
    }

    @Test
    void addComment_whenNotAuthorised_thenCommentInvalidException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        CommentReqDto commentRequestDto = new CommentReqDto();
        commentRequestDto.setText("comment");
        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);
        comment.setCreated(LocalDateTime.now().plusMinutes(1));
        comment.setAuthorName(user);
        comment.setItem(item);
        comment.setId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.MIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(InvalidRequestException.class, () -> itemService.addComment(commentRequestDto, userId, itemId));
    }

    @Test
    void addComment_whenCommentCreatedAfterBookingEnd_thenCommentInvalidException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        CommentReqDto commentRequestDto = new CommentReqDto();
        commentRequestDto.setText("comment");
        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);
        comment.setCreated(LocalDateTime.now().plusMinutes(1));
        comment.setAuthorName(user);
        comment.setItem(item);
        comment.setId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.MAX);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(InvalidRequestException.class, () -> itemService.addComment(commentRequestDto, userId, itemId));
    }

    @Test
    void addComment_whenCommentTextIsEmpty_thenCommentInvalidException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        CommentReqDto commentRequestDto = new CommentReqDto();
        commentRequestDto.setText("");
        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);
        comment.setCreated(LocalDateTime.now().plusMinutes(1));
        comment.setAuthorName(user);
        comment.setItem(item);
        comment.setId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.MIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(InvalidRequestException.class, () -> itemService.addComment(commentRequestDto, userId, itemId));
    }
}
