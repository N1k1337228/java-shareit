package ru.practicum.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.BaseClient;


@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Integer userId, RequestDto requestDto) {
        return post("", userId, null, requestDto);
    }

    public ResponseEntity<Object> getAllUsersResponses(Integer userId) {
        return get("", userId, null);
    }

    public ResponseEntity<Object> getAllResponses(Integer userId) {
        return get("/all", userId, null);
    }

    public ResponseEntity<Object> getRequestOnId(Integer requestId, Integer userId) {
        return get("/" + requestId, userId, null);
    }
}
