package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemJpaTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSearchForItems() {
        UserDto userDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        User owner = UserMapper.toUser(userDto);
        owner = userRepository.save(owner);

        Item item1 = ItemMapper.toItem(ItemDtoCreate.builder()
                .name("Item1")
                .description("Description1")
                .available(true).build());
        Item item2 = ItemMapper.toItem(ItemDtoCreate.builder()
                .name("Item2")
                .description("Description2")
                .available(true).build());

        item1.setOwner(owner);
        item2.setOwner(owner);

        itemRepository.save(item1);
        itemRepository.save(item2);

        String searchText = "item";

        List<Item> expectedList = new ArrayList<>();
        expectedList.add(item1);
        expectedList.add(item2);

        assertEquals(expectedList, itemRepository.searchItemByNameOrDescription(searchText, PageRequest.of(0, 2)));
    }

    @AfterEach
    public void delete() {
        itemRepository.deleteAll();
    }

}
