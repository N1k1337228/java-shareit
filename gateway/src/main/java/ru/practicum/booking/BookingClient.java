package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.BaseClient;



import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

//    public ResponseEntity<Object> getBookings(Integer userId, BookingState state, Integer from, Integer size) {
//        Map<String, Object> parameters = Map.of(
//                "state", state.name(),
//                "from", from,
//                "size", size
//        );
//        return get("?state={state}&from={from}&size={size}", userId, parameters);
//    }


    public ResponseEntity<Object> bookItem(Integer userId, BookingDto requestDto) {
        return post("", userId, null, requestDto);
    }

    public ResponseEntity<Object> getBooking(Integer userId, Integer bookingId) {

        return get("/" + bookingId, userId, null);
    }

    public ResponseEntity<Object> updateBooking(Integer userId, Integer bookingId, String approved) {
        Map<String, Object> parameters = Map.of(
                "approved",approved
        );
        return patch("/" +bookingId+"?approved={approved}",userId,parameters,null);
    }

    public ResponseEntity<Object> getBookingOnState(Integer userId, String state) {
        Map<String, Object> parameters = Map.of(
                "state",state
        );
        return get("?state={state}",userId,parameters);
    }

    public ResponseEntity<Object> getBookingOfCurrentUser(Integer userId, String state) {
        Map<String, Object> parameters = Map.of(
                "state",state
        );
        return get("/owner?state={state}",userId,parameters);
    }


}
