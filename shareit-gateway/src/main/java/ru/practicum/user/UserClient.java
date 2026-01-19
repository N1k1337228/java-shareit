package ru.practicum.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.BaseClient;


@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getUser(Integer userId) {
        return get("/" + userId, null, null);
    }

    public ResponseEntity<Object> updateUser(Integer userId, UserDto userDto) {
        return patch("/" + userId, userId, null, userDto);

    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("", null, null, userDto);

    }

    public ResponseEntity<Object> deleteUser(Integer userId) {
        return delete("/" + userId, null, null);
    }

}
