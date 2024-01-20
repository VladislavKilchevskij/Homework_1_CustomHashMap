import java.util.*;

public class CustomHashMap<K, V> implements Map<K, V> {
    public final int INIT_BUCKET_ARRAY_CAPACITY = 16;
    private final float LOAD_FACTOR = 0.75f;
    private Node<K, V>[] table;
    private int size;
    private int currentArrayCapacity;
    private int loadFactor;

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
            return (this.key.hashCode() + this.value.hashCode());
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

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.table = (Node<K, V>[]) new Node[INIT_BUCKET_ARRAY_CAPACITY];
        this.currentArrayCapacity = INIT_BUCKET_ARRAY_CAPACITY;
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public CustomHashMap(int capacity) {
        this.table = (Node<K, V>[]) new Node[capacity];
        this.currentArrayCapacity = capacity;
        this.size = 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        int keyHashCode = computeKeyHashCode(key);
        int indexOfBucket = computeBucketIndex(keyHashCode);
        if (table[indexOfBucket] == null) return null;
        for (Node<K, V> current = table[indexOfBucket]; current != null; current = current.next) {
            if (areKeysEqual(current.getKey(), key)) return current.getValue();
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        int keyHashCode = computeKeyHashCode(key);
        int indexOfBucket = computeBucketIndex(keyHashCode);
        Node<K, V> newNode = new Node<>(keyHashCode, key, value, null);
        if (table[indexOfBucket] == null) {
            table[indexOfBucket] = newNode;
            this.size++;
            return null;
        }

        Node<K, V> current;
        for (current = table[indexOfBucket]; current != null; current = current.next) {
            if (areKeysEqual(current.getKey(), key)) return current.setValue(value);
            if (current.next == null) break;
        }

        current.next = newNode;
        this.size++;
        return null;
    }


    @Override
    public V remove(Object key) {
        int keyHashCode = computeKeyHashCode(key);
        int indexOfBucket = computeBucketIndex(keyHashCode);
        Node<K, V> current;
        if ((current = table[indexOfBucket]) == null) return null;

        if (areKeysEqual(current.getKey(), key)) {
            V oldValue = current.getValue();
            table[indexOfBucket] = current.next;
            size--;
            return oldValue;
        }

        for (; current != null; current = current.next) {
            Node<K, V> verifiable = current.next;
            if (areKeysEqual(verifiable.getKey(), key)) {
                V oldValue = verifiable.getValue();
                current.next = verifiable.next;
                size--;
                return oldValue;
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        if (table != null && size > 0) {
            size = 0;
            Arrays.fill(table, null);
        }
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
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
                }
                sb.append("]\n");
            }
        }
        sb.append("Количество элементоа Мэп: ").append(this.size);
        return sb.toString();
    }

    private static boolean areKeysEqual(Object firstKey, Object secondKey) {
        firstKey = firstKey == null ? 0 : firstKey;
        secondKey = secondKey == null ? 0 : secondKey;
        return firstKey.hashCode() == secondKey.hashCode() && Objects.equals(firstKey, secondKey);
    }

    private int computeKeyHashCode(Object key) {
        return key == null ? 0 : key.hashCode();
    }


    private int computeBucketIndex(int keyHash) {
        return keyHash == 0 ? 0 : keyHash % this.currentArrayCapacity;
    }
}
