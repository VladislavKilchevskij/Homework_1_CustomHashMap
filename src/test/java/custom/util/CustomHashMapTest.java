package custom.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashMapTest {

    private Map<Integer, String> map;
    private Map<Integer, String> emptyMap;
    private final int MAP_SIZE = 100_000;
    private final Integer NOT_NULL_KEY = 1;
    private final String VALUE_FOR_NULL_KEY = "valueMappedToNullKey";
    private final String VALUE_FOR_NOT_NULL_KEY = "val" + NOT_NULL_KEY;

    @BeforeEach
    void prepare() {
        emptyMap = new CustomHashMap<>();
        map = new CustomHashMap<>();
        for (int i = 1; i < MAP_SIZE; i++) {
            map.put(i, "val" + i);
        }
        map.put(null, VALUE_FOR_NULL_KEY);
    }

    @Test
    void sizeWhenEmptyAndNotTest() {
        assertAll(
                () -> assertEquals(MAP_SIZE, map.size()),
                () -> assertEquals(0, emptyMap.size())
        );
    }

    @Test
    void isEmptyWhenEmptyAndNotEmptyTest() {
        assertAll(
                () -> assertTrue(emptyMap.isEmpty()),
                () -> assertFalse(map.isEmpty())
        );
    }

    @Test
    void containsKeyTest() {
        assertAll(
                () -> assertFalse(emptyMap.containsKey(NOT_NULL_KEY)),
                () -> assertFalse(emptyMap.containsKey(null)),
                () -> assertTrue(map.containsKey(NOT_NULL_KEY)),
                () -> assertTrue(map.containsKey(null))
        );
    }

    @Test
    void containsValueTest() {
        assertAll(
                () -> assertFalse(emptyMap.containsValue(VALUE_FOR_NOT_NULL_KEY)),
                () -> assertFalse(emptyMap.containsValue(VALUE_FOR_NOT_NULL_KEY)),
                () -> assertTrue(map.containsValue(VALUE_FOR_NOT_NULL_KEY)),
                () -> assertTrue(map.containsValue(VALUE_FOR_NOT_NULL_KEY))
        );
    }

    @Test
    void getWhenNullKeyOrNotNullKeyOrHaveNotMappingsTest() {
        assertAll(
                () -> assertNull(emptyMap.get(NOT_NULL_KEY)),
                () -> assertNull(emptyMap.get(null)),
                () -> assertEquals(VALUE_FOR_NOT_NULL_KEY, map.get(NOT_NULL_KEY)),
                () -> assertEquals(VALUE_FOR_NULL_KEY, map.get(null))
        );
    }

    @Test
    void putNewMappingWithNullAndNotNullKeyTest() {
        String actualWhenPutByNotNullKey = emptyMap.put(NOT_NULL_KEY, VALUE_FOR_NOT_NULL_KEY);
        int actualSizeAfterPutNotNullKey = emptyMap.size();
        String actualWhenPutByNullKey = emptyMap.put(null, VALUE_FOR_NULL_KEY);
        int actualSizeAfterPutNullKey = emptyMap.size();

        assertAll(
                () -> assertNull(actualWhenPutByNotNullKey),
                () -> assertEquals(VALUE_FOR_NOT_NULL_KEY, map.get(NOT_NULL_KEY)),
                () -> assertEquals(1, actualSizeAfterPutNotNullKey),
                () -> assertNull(actualWhenPutByNullKey),
                () -> assertEquals(VALUE_FOR_NULL_KEY, map.get(null)),
                () -> assertEquals(2, actualSizeAfterPutNullKey)
        );
    }

    @Test
    void updateValueWhenNullAndNotNullKeyIsPresentTest() {
        String actualValueForNotNullKey = map.put(NOT_NULL_KEY, "new_value");
        int actualSizeAfterUpdateByNotNullKey = map.size();
        String expectedOldValueForNotNullKey = VALUE_FOR_NOT_NULL_KEY;
        String actualValueForNullKey = map.put(null, "new_value_for_null_key");
        int actualSizeAfterUpdateByNullKey = map.size();
        String expectedOldValueForNullKey = VALUE_FOR_NULL_KEY;
        assertAll(
                () -> assertEquals(expectedOldValueForNotNullKey, actualValueForNotNullKey),
                () -> assertEquals("new_value", map.get(NOT_NULL_KEY)),
                () -> assertEquals(MAP_SIZE, actualSizeAfterUpdateByNotNullKey),
                () -> assertEquals(expectedOldValueForNullKey, actualValueForNullKey),
                () -> assertEquals("new_value_for_null_key", map.get(null)),
                () -> assertEquals(MAP_SIZE, actualSizeAfterUpdateByNullKey)
        );
    }

    @Test
    void removeByNullAndNotNullKeyTest() {
        String actualRemovedValueForNotNullKey = map.remove(NOT_NULL_KEY);
        String expectedRemovedValueForNotNullKey = VALUE_FOR_NOT_NULL_KEY;
        int actualSizeAfterRemoveByNotNullKey = map.size();
        String actualRemovedValueForNullKey = map.remove(null);
        String expectedRemovedValueForNullKey = VALUE_FOR_NULL_KEY;
        int actualSizeAfterRemoveByNullKey = map.size();
        assertAll(
                () -> assertEquals(expectedRemovedValueForNotNullKey, actualRemovedValueForNotNullKey),
                () -> assertEquals(MAP_SIZE - 1, actualSizeAfterRemoveByNotNullKey),
                () -> assertEquals(expectedRemovedValueForNullKey, actualRemovedValueForNullKey),
                () -> assertEquals(MAP_SIZE - 2, actualSizeAfterRemoveByNullKey)
        );
    }

    @Test
    void removeWhenNoMappingTest() {
        String actualValueAfterRemoveByNotNullKey = emptyMap.remove(NOT_NULL_KEY);
        int actualSizeAfterRemoveByNotNullKey = emptyMap.size();
        String actualValueAfterRemoveByNullKey = emptyMap.remove(null);
        int actualSizeAfterRemoveByNullKey = emptyMap.size();

        assertAll(
                () -> assertNull(actualValueAfterRemoveByNotNullKey),
                () -> assertEquals(0, actualSizeAfterRemoveByNotNullKey),
                () -> assertNull(actualValueAfterRemoveByNullKey),
                () -> assertEquals(0, actualSizeAfterRemoveByNullKey)
        );
    }

    @Test
    void clearTest() {
        map.clear();
        assertEquals(0, map.size());
    }

    @Test
    void putAllWhenNullEmptyAndFilledMapArePresentedTest() {
        Map<Integer, String> mapToBeAdded = new CustomHashMap<>();
        for (int i = MAP_SIZE + 1; i < MAP_SIZE * 2; i++) {
            mapToBeAdded.put(i, "val" + i);
        }
        map.putAll(null);
        int actualResultMapSizeWhenNullMapAdded = map.size();
        map.putAll(emptyMap);
        int actualResultMapSizeWhenEmptyMapAdded = map.size();

        int expectedResultMapSize = map.size() + mapToBeAdded.size();
        map.putAll(mapToBeAdded);
        int actualResultMapSize = map.size();

        assertAll(
                () -> assertEquals(MAP_SIZE, actualResultMapSizeWhenNullMapAdded),
                () -> assertEquals(MAP_SIZE, actualResultMapSizeWhenEmptyMapAdded),
                () -> assertEquals(expectedResultMapSize, actualResultMapSize)
        );
    }

    @Test
    void keySetTest() {
        Set<Integer> emptyMapKeySet = emptyMap.keySet();
        Set<Integer> mapKeySet = map.keySet();
        assertAll(
                () -> assertNotNull(emptyMapKeySet),
                () -> assertEquals(emptyMap.size(), emptyMapKeySet.size()),
                () -> assertNotNull(mapKeySet),
                () -> assertEquals(map.size(), mapKeySet.size())
        );
    }

    @Test
    void valuesTest() {
        Collection<String> emptyMapValues = emptyMap.values();
        Collection<String> mapValues = map.values();
        assertAll(
                () -> assertNotNull(emptyMapValues),
                () -> assertEquals(emptyMap.size(), emptyMapValues.size()),
                () -> assertNotNull(mapValues),
                () -> assertEquals(map.size(), mapValues.size())
        );
    }

    @Test
    void entrySetTest() {
        Set<Map.Entry<Integer, String>> emptyMapEntries = emptyMap.entrySet();
        Set<Map.Entry<Integer, String>> mapEntries = map.entrySet();
        assertAll(
                () -> assertNotNull(emptyMapEntries),
                () -> assertEquals(emptyMap.size(), emptyMapEntries.size()),
                () -> assertNotNull(mapEntries),
                () -> assertEquals(map.size(), mapEntries.size())
        );
    }
}