package custom.util;

import java.util.*;

/**
 * Реализация интерфейса Map, основанная на хэш-таблице. Данная реализация Map не потокобезопасна.
 * С целью оптимизации операции поиска элемента по индексу, реализован механизм перехэширования.
 *
 * @param <K> тип ключа
 * @param <V> тип, связанного с ключом значения
 * @author Владислав Кильчевский
 */
public class CustomHashMap<K, V> implements Map<K, V> {

    /**
     * Значение размера хэш-таблицы по умолчанию.
     */
    public static final int INIT_BUCKET_ARRAY_CAPACITY = 16;

    /**
     * Значение коэффициента заполнения хэш-таблицы по умолчанию.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Node<K, V>[] table;
    private int size;
    private int tableCapacity;

    private final float loadFactor;

    /**
     * Отражает количество элементов, по пдостижению которого происходит перехэширование.
     * Целочисленное значение вычисляется по формуле: tableCapacity * loadFactor.
     */
    private int growBorder;

    /**
     * Конструктор, для создания объекта CustomHashMap с возможностью указать изначальный размер хэш-таблиы,
     * а также определить значение коэффициента заполнения.
     *
     * @param capacity размер хэш-таблицы
     * @param load     коэффициент заполнения
     */
    public CustomHashMap(int capacity, float load) {
        this.table = (Node<K, V>[]) new Node[capacity];
        this.tableCapacity = capacity;
        this.loadFactor = load;
        this.growBorder = (int) (capacity * this.loadFactor);
        this.size = 0;
    }

    /**
     * Конструктор, для создания объекта CustomHashMap с возможностью указать изначальный размер хэш-таблиы.
     * Значение коэффициента заполнения инициализирется полем по умолчанию.
     *
     * @param capacity размер хэш-таблицы
     */
    public CustomHashMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Конструктор, для создания объекта CustomHashMap по умолчанию.
     * Значение размера хэш-таблиы и значение коэффициента заполнения инициализируются значениями по умолчанию.
     */
    public CustomHashMap() {
        this(INIT_BUCKET_ARRAY_CAPACITY);
    }

    /**
     * Реализация интерфейса Map.Entry<K, V>, экземпляры которого хранят пары ключ=значение,
     * хэш-код ключа, высчитываемый при добавлении, а также ссылку на следующий элемент списка.
     *
     * @param <K> тип ключа
     * @param <V> тип, связанного с ключом значения
     */
    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public final K getKey() {
            return this.key;
        }

        @Override
        public final V getValue() {
            return this.value;
        }

        @Override
        public final V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode() + this.value.hashCode());
        }

        @Override
        public final boolean equals(Object object) {
            if (this == object) return true;
            if (object == null) return false;
            return object instanceof Map.Entry<?, ?> entry
                    && Objects.equals(this.key, entry.getKey())
                    && Objects.equals(this.value, entry.getValue());
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    /**
     * Механизм перехэширования  хэш-таблиы.
     * При условии, что текущее количество элементов в CustomHashMap достигло значения growBorder, определяемого как:
     * размер хэш-таблицы * коэффициент заполнения, просиходит создание нового экземпляра хэш-таблицы с увеличенной
     * в 2 раза размерностью, вычислением нового значения для поля growBorder, а также перемещением элементов
     * одновременно с пересчётом хэш-кода ключей.
     */
    private void growMapIfAchieveBorder() {
        if (size == growBorder) {
            Node<K, V>[] temp = table;
            size = 0;
            tableCapacity = tableCapacity * 2;
            growBorder = (int) (tableCapacity * loadFactor);
            table = (Node<K, V>[]) new Node[tableCapacity];
            for (Node<K, V> node : temp) {
                for (; node != null; node = node.next) {
                    putMapping(node.getKey(), node.getValue());
                }
            }
        }
    }

    /**
     * Возвращает количество пар ключ-значение.
     *
     * @return количество пар ключ-значение.
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Возвращает true, если CustomHashMap содержит 1 и более пар ключ-значение. В противном случае возвращает false.
     *
     * @return true, если CustomHashMap содержит 1 и более пар ключ-значение. В противном случае возвращает false.
     */
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Возвращает true, если CustomHashMap содержит пару ключ-значение, ключ которой соответствует key. В противном случае возвращает false.
     * Для key допустимо null. CustomHashMap может хранить лишь 1 пару ключ-значение, ключом которой является null.
     *
     * @param key ключ, наличие которого проверяется в CustomHashMap
     * @return true - если ключ содержится в CustomHashMap. В обратно случае false
     */
    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /**
     * Возвращает true, если CustomHashMap содержит пару ключ-значение, значение которой соответствует value. В противном случае возвращает false.
     * Передача null в качестве value, приведёт к NullPointerException.
     *
     * @param value значение, наличие которого проверяется в CustomHashMap
     * @return true - если значение содержится в CustomHashMap. В обратно случае false
     */
    @Override
    public boolean containsValue(Object value) {
        if (table != null) {
            for (Node<K, V> node : table) {
                for (; node != null; node = node.next) {
                    if (Objects.equals(node.getValue(), value)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Возвращает значение, в случае наличия в CustomHashMap пары ключ-значение, ключ которой соответствует key. В противном случае - null.
     *
     * @param key ключ, значение которого должно быть возвращено
     * @return возвращает значение, в случае наличия key в CustomHashMap. В противном случае - null.
     */
    @Override
    public V get(Object key) {
        int keyHashCode = hash(key);
        int indexOfBucket = computeBucketIndex(keyHashCode);
        if (table[indexOfBucket] == null) return null;
        for (Node<K, V> current = table[indexOfBucket]; current != null; current = current.next) {
            if (Objects.equals(current.getKey(), key)) return current.getValue();
        }
        return null;
    }

    /**
     * Выполняет добавление пары ключ-значение, если пары с указанным ключом не существует. В противном случае
     * выполняется обновление пары ключ-значение в виде перезаписи существующего значения, указанным.
     *
     * @param key   ключ, который должен быть ассоциирован с указанным значением
     * @param value значение, которое должно быть ассоциировано с указанным ключом
     * @return возвращает null, если указанная пара ключ-значение отсутствует. Значение, которое ранее
     * ассоциировалось с указанным ключом, если указанный ключ присутсвует.
     */
    @Override
    public V put(K key, V value) {
        if (value == null) throw new NullPointerException();
        int oldSize = size;
        V oldValue = putMapping(key, value);
        if (size > oldSize) growMapIfAchieveBorder();
        return oldValue;
    }

    private V putMapping(K key, V value) {
        int keyHashCode = hash(key);
        int indexOfBucket = computeBucketIndex(keyHashCode);
        Node<K, V> newNode = new Node<>(keyHashCode, key, value, null);
        if (table[indexOfBucket] == null) {
            table[indexOfBucket] = newNode;
            this.size++;
            return null;
        }

        Node<K, V> current;
        for (current = table[indexOfBucket]; current != null; current = current.next) {
            if (Objects.equals(current.getKey(), key)) return current.setValue(value);
            if (current.next == null) break;
        }

        assert current != null;
        current.next = newNode;
        this.size++;
        return null;
    }

    /**
     * Выполняет удаление пары ключ-значение, если пара с указанным ключом присутствует.
     *
     * @param key ключ пары ключ-значение, которая должна быть удалена
     * @return возвращает null, если пара ключ-значение с указанным ключом отсутствует. Значение, удалённой
     * пары ключ-значение.
     */
    @Override
    public V remove(Object key) {
        int keyHashCode = hash(key);
        int indexOfBucket = computeBucketIndex(keyHashCode);
        Node<K, V> current;
        if ((current = table[indexOfBucket]) == null) return null;

        if (Objects.equals(current.getKey(), key)) {
            V oldValue = current.getValue();
            table[indexOfBucket] = current.next;
            size--;
            return oldValue;
        }

        for (; current != null; current = current.next) {
            Node<K, V> verifiable = current.next;
            if (Objects.equals(verifiable.getKey(), key)) {
                V oldValue = verifiable.getValue();
                current.next = verifiable.next;
                size--;
                return oldValue;
            }
        }
        return null;
    }

    /**
     * Выполняет добавление ассоциативного массива пар ключ-значение, если пар с указанным ключом не существует. В противном случае
     * выполняется обновление пар ключ-значение в виде перезаписи существующих значений, представленными в указанном
     * ассоциативном массиве.
     *
     * @param map   ассоциативный массив, пары ключ-значения которого долдны быть добавлены
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        if (map != null && map.size() > 0) {
            for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Удаляет все элементы хэш-таблицы.
     */
    @Override
    public void clear() {
        if (table != null && size > 0) {
            size = 0;
            Arrays.fill(table, null);
        }
    }

    /**
     * Возвращает множество хранящихся в Map ключей.
     * @return возвращает Set ключей, представленных в Map
     */
    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        if (size > 0) {
            for (Node<K, V> node : table) {
                for (; node != null; node = node.next) {
                    keySet.add(node.getKey());
                }
            }
        }
        return keySet;
    }

    /**
     * Возвращает множество хранящихся в Map значений.
     * @return возвращает Collection значений, представленных в Map
     */
    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();
        if (size > 0) {
            for (Node<K, V> node : table) {
                for (; node != null; node = node.next) {
                    values.add(node.getValue());
                }
            }
        }
        return values;
    }
    /**
     * Возвращает множество хранящихся в Map пар ключ-значение.
     * @return Set пар ключ-значение, представленных в Map
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = new HashSet<>();
        if (size > 0) {
            for (Node<K, V> node : table) {
                for (; node != null; node = node.next) {
                    entrySet.add(node);
                }
            }
        }
        return entrySet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < this.table.length; index++) {
            if (table[index] == null) sb.append("Бакет-").append(index).append(" пустой\n");
            else {
                sb.append("Бакет-").append(index).append(" [");
                for (Node<K, V> current = table[index]; current != null; current = current.next) {
                    sb.append("{").append(current.getKey()).append("=").append(current.getValue()).append("}");
                    if (current.next != null) sb.append(" -> ");
                }
                sb.append("]\n");
            }
        }
        sb.append("Количество элементоа Мэп: ").append(this.size);
        return sb.toString();
    }

    /**
     * Высчитывает целочисленный уникальный код предоставленного ключа и размывает его значение для минимизации коллизий.
     * @param key ключ, хэш-код которого должен быть высчитан
     * @return целочисленный уникальный код key
     */
    private static int hash(Object key) {
        return key == null ? 0 : key.hashCode() * 17 * 31;
    }

    /**
     * Определяет индекс бакета в хэ-таблице на основании предоставленного хэш-кода.
     * @param keyHash хэш-код ключа для определения индекса бакета
     * @return индекс бакета в хэш-таблице, на основании предоставленного хэш-кода
     */
    private int computeBucketIndex(int keyHash) {
        return keyHash == 0 ? 0 : keyHash % this.tableCapacity;
    }
}