package ru.practicum.shareit.item.itemService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.bookingRepository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        User user = checkUser(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
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
    public List<ItemDto> getItemsByOwner(long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<Long> itemIds = extractItemIds(items);
        List<ItemDto> itemDtoList = getItemsWithCommentsForItemIds(items, itemIds);

        itemDtoList.forEach(i -> {
            List<BookingDto> nextBookingClosest = bookingRepository.findNextClosestBookingByOwnerId(ownerId, i.getId());
            List<BookingDto> lastBookingClosest = bookingRepository.findLastClosestBookingByOwnerId(ownerId, i.getId());

            i.setNextBooking(nextBookingClosest.isEmpty() ? null : nextBookingClosest.get(0));
            i.setLastBooking(lastBookingClosest.isEmpty() ? null : lastBookingClosest.get(0));
        });

        return itemDtoList;
    }

    @Override
    @Transactional
    public ItemDto updateItemData(long itemId, long ownerId, ItemDto itemDto) {
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
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) return new ArrayList<>();
        List<Item> items = itemRepository.searchItemByNameOrDescription(text);
        List<Long> itemIds = extractItemIds(items);
        return getItemsWithCommentsForItemIds(items, itemIds);
    }

    @Override
    public void removeItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentResDto addComment(CommentReqDto commentRequestDto, long bookerId, long itemId) {
        User user = checkUser(bookerId);
        Item item = checkItem(itemId);

        List<Booking> bookings = bookingRepository.findAllByBookerIdPast(bookerId);
        Booking booking = bookings.stream()
                .filter(b -> b.getItem().getId() == itemId)
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
}
