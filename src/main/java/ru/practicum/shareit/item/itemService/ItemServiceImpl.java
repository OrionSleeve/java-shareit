package ru.practicum.shareit.item.itemService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.bookingRepository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.pagination.Paginator;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto createItem(long ownerId, ItemDtoCreate itemDto) {
        User user = checkUser(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        Long requestId = itemDto.getRequestId();
        ItemRequest request = requestId != null ? itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request " + requestId + " not found")) : null;
        item.setRequest(request);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(long itemId, long ownerId) {
        Item item = checkItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        itemDto.setComments(CommentMapper.toCommentListResponseDto(comments));

        if (itemDto.getOwnerId() == ownerId) {
            BookingDto nextBooking = bookingRepository.findNextClosestBookingByOwnerId(ownerId, itemId)
                    .stream().findFirst().orElse(null);
            itemDto.setNextBooking(nextBooking);

            BookingDto lastBooking = bookingRepository.findLastClosestBookingByOwnerId(ownerId, itemId)
                    .stream().findFirst().orElse(null);
            itemDto.setLastBooking(lastBooking);
        }
        return itemDto;

    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwner(long ownerId, int from, int size) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId, Paginator.createSimplePageRequest(from, size));
        List<Long> itemIds = extractItemIds(items);
        List<ItemDto> itemDtoList = getItemsWithCommentsForItemIds(items, itemIds);
        List<Booking> allBookingsForItems = findAllBookingsForItems(itemIds);

        for (ItemDto i : itemDtoList) {
            BookingDto nextBooking = BookingMapper.maptoBookingDtoForOwner(findNextBooking(allBookingsForItems, i.getId()));
            BookingDto lastBooking = BookingMapper.maptoBookingDtoForOwner(findLastBooking(allBookingsForItems, i.getId()));
            i.setNextBooking(nextBooking);
            i.setLastBooking(lastBooking);
        }
        return itemDtoList;
    }

    @Override
    @Transactional
    public ItemDto updateItemData(long itemId, long ownerId, ItemDtoCreate itemDto) {
        checkUser(ownerId);
        Item updatedItem = ItemMapper.toItem(itemDto);
        itemRepository.updateItemFields(updatedItem, ownerId, itemId);
        ItemDto updatedItemDto = ItemMapper.toItemDto(checkItem(itemId));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        updatedItemDto.setComments(CommentMapper.toCommentListResponseDto(comments));
        return updatedItemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isBlank()) return Collections.emptyList();
        List<Item> items = itemRepository.searchItemByNameOrDescription(text, Paginator.createSimplePageRequest(from, size));
        List<Long> itemIds = extractItemIds(items);
        return getItemsWithCommentsForItemIds(items, itemIds);
    }

    @Override
    @Transactional
    public void removeItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentResDto addComment(CommentReqDto commentRequestDto, long bookerId, long itemId) {
        User user = checkUser(bookerId);
        Item item = checkItem(itemId);

        Booking booking = bookingRepository.findAllByBookerIdAndItemIdPast(bookerId, itemId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Booking from user " + bookerId
                        + " for item " + itemId + " doesn't exist"));

        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(1);
        comment.setCreated(dateTime);
        if (booking.getEnd().isAfter(comment.getCreated())) {
            throw new InvalidRequestException("Comment field created must be after booking end");
        }
        if (comment.getText().isEmpty()) {
            throw new InvalidRequestException("Comment cannot be empty");
        }
        comment.setItem(item);
        comment.setAuthorName(user);
        commentRepository.save(comment);
        return CommentMapper.toCommentResponseDto(comment);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with userId: " + userId));
    }

    private Item checkItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found."));
    }

    private List<Long> extractItemIds(List<Item> items) {
        return items.stream().map(Item::getId).collect(Collectors.toList());
    }


    private List<ItemDto> getItemsWithCommentsForItemIds(List<Item> items, List<Long> id) {
        Map<Long, List<Comment>> commentsMap = commentRepository.findAllByItemIdIn(id)
                .stream().collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : items) {
            ItemDto dto = ItemMapper.toItemDto(item);
            dto.setComments(commentsMap.getOrDefault(item.getId(), Collections.emptyList())
                    .stream().map(CommentMapper::toCommentResponseDto).collect(Collectors.toList()));
            itemDtoList.add(dto);
        }
        return itemDtoList;
    }

    private Booking findLastBooking(List<Booking> allBookingsForItems, long itemId) {
        Optional<Booking> lastBooking = allBookingsForItems.stream()
                .filter(booking -> booking.getItem().getId() == itemId)
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .filter(booking -> booking.getStatus() == Status.APPROVED)
                .max(Comparator.comparing(Booking::getEnd));

        return lastBooking.orElse(null);
    }

    private Booking findNextBooking(List<Booking> allBookingsForItems, long itemId) {
        Optional<Booking> nextBooking = allBookingsForItems.stream()
                .filter(booking -> booking.getItem().getId() == itemId)
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .filter(booking -> booking.getStatus() == Status.APPROVED)
                .min(Comparator.comparing(Booking::getStart));

        return nextBooking.orElse(null);
    }

    private List<Booking> findAllBookingsForItems(List<Long> itemIds) {
        return bookingRepository.findAllByItemIdIn(itemIds);
    }
}
