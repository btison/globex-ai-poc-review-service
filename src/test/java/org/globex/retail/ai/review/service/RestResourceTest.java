package org.globex.retail.ai.review.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class RestResourceTest {

    @InjectMock
    KafkaService kafkaService;

    @Test
    public void testSubmitReviewCorrectPayload() {
        String payload = """
                {
                "id": "qwertyuiop",
                "user": "mmaddox",
                "product_code": "101808",
                "product": "Vic Firth 5A American Classic Hickory",
                "review": "The \\"default\\" drumstick. \\n To me, the Vic Firth 5A American Classics are the standard of drumsticks. These are well-balanced, well-matched, great sounding drumsticks. As a drummer of small stature with hands that are on the small size, I find these sticks to be of just the right size, both in length and diameter, and weight for general playing. The bead of the stick also gives a good, clear sound on the cymbals. For slower, heavier songs I prefer a 5B or other bigger model, but for faster stuff and for lighter playing that requires less volume, these are great. \\n On the negative side, I find that while these sticks hold up well even under fairly heavy playing with constant rimshot backbeats, they're not as durable as e.g. Pro-Mark's equivalent 5A model (though I've never had a Vic 5A snap on me within minutes of starting playing, as has happened with some other sticks). The wood tips also have a tendency to chip, so they may not be intact at the end of a 50-minute set.",
                "stars": 5,
                "created": "2025-08-12T16:23:09Z"
                }
                """;
        RestAssured.given().when().with().body(payload).header(new Header("Content-Type", "application/json"))
                .post("/review/submit")
                .then().assertThat().statusCode(200).body(Matchers.equalTo(""));

        Mockito.verify(kafkaService).emit("qwertyuiop", payload);
    }

}
