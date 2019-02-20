package grails.plugin.externalconfig

import groovy.transform.CompileStatic

@CompileStatic
class WriteFilteringMap implements Map<String, Object> {

    String keyPrefix
    private Map<String, Object> proxied // source map
    @Delegate private Map<String, Object> overlap  // written values, flattened -- shared
    private Map<String, Object> nestedDestinationMap // written keys at this level

    WriteFilteringMap(Map source = [:]) {
        this(source, '', [:])
    }

    private WriteFilteringMap(Map nestedSource, String nestedKey, Map destination) {
        this.proxied = nestedSource
        this.keyPrefix = nestedKey
        this.nestedDestinationMap = destination
        initialize()
    }

    Map<String, Object> getWrittenValues() {
        return nestedDestinationMap.asImmutable()
    }

    private Map<String, Object> initialize() {
        overlap = [:]
        proxied.each { String k, Object original ->
            if (original == null || original in Map) {
                overlap.put(k, new WriteFilteringMap(
                        (original ?: Collections.emptyMap()) as Map,
                        keyPrefix + k + '.', nestedDestinationMap))
            } else {
                overlap.put(k, original)
            }
        }
        return overlap
    }

    /**
     * The map is infinite, either returning a value or an empty next level map
     * @param key
     * @return current value or an empty map
     */
    @Override
    Object get(Object key) {
        return overlap.get(key) ?: new WriteFilteringMap(
                Collections.emptyMap(),
                keyPrefix + key + '.', nestedDestinationMap)
    }

    @Override
    Object put(String key, Object value) {
        nestedDestinationMap.put(keyPrefix + key, value)
        return overlap.put(key, value)
    }

    @Override
    Object remove(Object key) {
        nestedDestinationMap.remove(keyPrefix + key)
        return overlap.remove(key)
    }

    @Override
    void putAll(Map<? extends String, ?> m) {
        m.each { k, v ->
            this.put(k, v)
        }
    }
}
