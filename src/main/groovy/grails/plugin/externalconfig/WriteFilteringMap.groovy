package grails.plugin.externalconfig

import groovy.transform.CompileStatic

@CompileStatic
class WriteFilteringMap implements Map<String, Object> {

    String keyPrefix
    Map<String, Object> proxied // source map
    Map<String, Object> overlayMap = null // written values, flattened -- shared
    Map<String, Object> nestedDestinationMap // written keys at this level

    WriteFilteringMap(Map source) {
        this.keyPrefix = ''
        this.proxied = source
        this.nestedDestinationMap = [:]
    }

    private WriteFilteringMap(Map nestedSource, String nestedKey, Map destination) {
        this.proxied = nestedSource
        this.keyPrefix = nestedKey
        this.nestedDestinationMap = destination
    }

    public Map<String, Object> getWrittenValues() {
        return nestedDestinationMap.asImmutable()
    }

    private Map<String, Object> getOverlap() {
        if (overlayMap == null) {
            overlayMap = [:]
            proxied.each { String k, Object original ->
                if (original == null || original in Map) {
                    if (original == null) original = Collections.emptyMap()
                    overlayMap.put(k, new WriteFilteringMap(
                            original as Map,
                            keyPrefix + k + '.', nestedDestinationMap))
                } else {
                    overlayMap.put(k, original)
                }
            }
        }
        return overlayMap
    }

    @Override
    int size() {
        return getOverlap().size()
    }

    @Override
    boolean isEmpty() {
        return getOverlap().size() == 0;
    }

    @Override
    boolean containsKey(Object key) {
        return getOverlap().containsKey(key);
    }

    @Override
    boolean containsValue(Object value) {
        return getOverlap().containsValue(value)
    }

    @Override
    Object get(Object key) {
        return getOverlap().get(key)
    }

    @Override
    Object put(String key, Object value) {
        nestedDestinationMap.put(keyPrefix + key, value)
        return getOverlap().put(key, value)
    }

    @Override
    Object remove(Object key) {
        nestedDestinationMap.remove(keyPrefix + key)
        return getOverlap().remove(key)
    }

    @Override
    void putAll(Map<? extends String, ?> m) {
        m.each { k,v ->
            this.put(k, v)
        }
    }

    @Override
    void clear() {
        getOverlap().clear()
    }

    @Override
    Set<String> keySet() {
        return getOverlap().keySet()
    }

    @Override
    Collection<Object> values() {
        return getOverlap().values()
    }

    @Override
    Set<Entry<String, Object>> entrySet() {
        return getOverlap().entrySet()
    }
}
