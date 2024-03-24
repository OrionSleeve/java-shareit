package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.item.dto.CommentReqDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long ownerId, ItemDtoCreate itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> getItemById(long itemId, long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getItemsByOwner(long ownerId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> updateItemData(long itemId, long ownerId, ItemDtoCreate itemDto) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> searchItems(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> removeItem(long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> addComment(long itemId, long bookerId, CommentReqDto commentRequestDto) {
        return post("/" + itemId + "/comment", bookerId, commentRequestDto);
    }

}