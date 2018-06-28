interface Cache<Key, Value> {
    Value get(Key key)
    void set(Key key,Value value)
}