import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashMapTest {

    private Map<Integer, String> map;
    private final Integer TEST_KEY = 1;
    private final String TEST_VALUE = "one";


    @BeforeEach
    void init() {
        map = new CustomHashMap<>();
    }

    @AfterEach
    void clearTestData() {
        map.clear();
    }

    @Test
    void sizeTest() {
        assertAll(
                () -> assertEquals(0, map.size()),
                () -> {
                    map.put(TEST_KEY, TEST_VALUE);
                    assertEquals(1, map.size());
                }
        );
    }

    @Test
    void isEmptyWhenEmptyAndNotEmptyTest() {
        assertAll(
                () -> assertTrue(map.isEmpty()),
                () -> {
                    map.put(TEST_KEY, TEST_VALUE);
                    assertFalse(map.isEmpty());
                }
        );
    }

    @Test
    void containsKeyReturnTrueTest() {
        assertAll(
                () -> assertFalse(map.containsKey(TEST_KEY)),
                () -> assertFalse(map.containsKey(null)),
                () -> {
                    map.put(TEST_KEY, TEST_VALUE);
                    assertTrue(map.containsKey(1));
                },
                () -> {
                    map.put(null, TEST_VALUE);
                    assertTrue(map.containsKey(null));
                }
        );
    }

    @Test
    void getWhenNullKeyOrNotNullKeyOrHaveNotMappingsTest() {
        assertAll(
                () -> assertNull(map.get(TEST_KEY)),
                () -> {
                    map.put(null, TEST_VALUE);
                    assertEquals(TEST_VALUE, map.get(null));
                },
                () -> {
                    map.put(TEST_KEY, TEST_VALUE);
                    assertEquals(TEST_VALUE, map.get(TEST_KEY));
                }
        );
    }

    @Test
    void putNewMappingWithNotNullKeyTest() {
        String actual = map.put(TEST_KEY, TEST_VALUE);
        assertAll(
                () -> assertNull(actual),
                () -> assertEquals(TEST_VALUE, map.get(TEST_KEY))
        );
    }

    @Test
    void putNewMappingWithNullKeyTest() {
        String actual = map.put(null, TEST_VALUE);
        assertAll(
                () -> assertNull(actual),
                () -> assertEquals(TEST_VALUE, map.get(null))
        );
    }

    @Test
    void putAndUpdateValueWhenNotNullKeyIsPresentTest() {
        map.put(TEST_KEY, TEST_VALUE);
        String actual = map.put(TEST_KEY, "new_value");
        assertAll(
                () -> assertEquals(TEST_VALUE, actual),
                () -> assertEquals("new_value", map.get(TEST_KEY))
        );
    }

    @Test
    void putAndUpdateValueWhenNullKeyIsPresentTest() {
        map.put(null, TEST_VALUE);
        String actual = map.put(null, "new_value");
        assertAll(
                () -> assertEquals(TEST_VALUE, actual),
                () -> assertEquals("new_value", map.get(null))
        );
    }

    @Test
    void removeByNotNullKeyTest() {
        map.put(TEST_KEY, TEST_VALUE);
        int size = map.size();
        String actual = map.remove(TEST_KEY);
        assertAll(
                () -> assertEquals(TEST_VALUE, actual),
                () -> assertEquals(0, size - 1)
        );
    }

    @Test
    void removeByNullKeyTest() {
        map.put(null, TEST_VALUE);
        int size = map.size();
        String actual = map.remove(null);
        assertAll(
                () -> assertEquals(TEST_VALUE, actual),
                () -> assertEquals(0, size - 1)
        );
    }

    @Test
    void removeWhenNoMappingTest() {
        int size = map.size();
        String actual = map.remove(null);
        assertAll(
                () -> assertNull(actual),
                () -> assertEquals(0, size)
        );
    }

    @Test
    void clearTest() {
        map.put(TEST_KEY, TEST_VALUE);
        map.clear();
        assertAll(
                () -> assertEquals(0, map.size()),
                () -> assertSame(map, map)
        );
    }
}