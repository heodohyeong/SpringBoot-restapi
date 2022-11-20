package me.whiteship.demoinfleanrestapi.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;



public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("Inflearn Spring Rest API")
                .description("Rest API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }


    @Test
    public void javaBean(){
        //Given
        String name = "Event";
        String description = "Spring";

        //When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }


    @ParameterizedTest
    @MethodSource("paramsForTestFree")
    public void TestFree(int basePrice , int maxPrice , boolean isFree){
        //given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isFree()).isEqualTo(isFree);


        /*//given
        Event event2 = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();
        //when
        event.update();

        //then
        assertThat(event2.isFree()).isFalse();


        //given
        Event event3 = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();
        //when
        event.update();

        //then
        assertThat(event3.isFree()).isFalse();*/

    }
    private static Stream<Arguments> paramsForTestFree() { // argument source method
        return Stream.of(
                Arguments.of(0,0, true),
                Arguments.of(100, 0, false),
                Arguments.of(0, 100, false),
                Arguments.of(100, 200, false)
        );
    }

    private static Stream<Arguments> paramsForTestOffline() { // argument source method
        return Stream.of(
                Arguments.of("강남", true),
                Arguments.of(null, false),
                Arguments.of("        ", false)
        );
    }

    @ParameterizedTest
    @MethodSource("paramsForTestOffline")
    public void TestOffline(String location , boolean isOffline){
        //given
        Event event = Event.builder()
                .location(location)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }
}