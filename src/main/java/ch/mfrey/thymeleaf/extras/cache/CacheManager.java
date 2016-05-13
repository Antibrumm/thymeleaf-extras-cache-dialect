package ch.mfrey.thymeleaf.extras.cache;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.StandardCache;
import org.thymeleaf.templatemode.TemplateMode;

public class CacheManager {
    private static final String CACHE_NAME = "CacheDialect";

    public static final CacheManager INSTANCE = new CacheManager();

    private static final Logger log = LoggerFactory.getLogger(CacheManager.class);

    private static String getCacheName(final String name, final TemplateMode templateMode, final Locale locale) {
        return name + "_" + templateMode + "_" + locale;
    }

    private volatile ICache<String, List<String>> cache;
    private volatile boolean cacheInitialized = false;

    public void evict(final String cacheName, final TemplateMode templateMode, final Locale locale) {
        getCache().clearKey(getCacheName(cacheName, templateMode, locale));
    }

    public void evictByStartsWith(final String cacheName) {
        for (String key : cache.keySet()) {
            if (key.startsWith(cacheName)) {
                getCache().clearKey(key);
            }
        }
    }

    public List<String> get(final String cacheName, final TemplateMode templateMode, final Locale locale,
            final int cacheTTLs) {
        if (cacheTTLs == 0) {
            return getCache().get(getCacheName(cacheName, templateMode, locale));
        } else {
            return getCache().get(getCacheName(cacheName, templateMode, locale),
                    new TTLCacheValidityChecker(cacheTTLs));
        }
    }

    private final ICache<String, List<String>> getCache() {
        if (!this.cacheInitialized) {
            synchronized (this) {
                if (!this.cacheInitialized) {
                    initializeCache();
                    this.cacheInitialized = true;
                }
            }
        }
        return this.cache;
    }

    public String getCacheNameFromExpressionResult(final Object expressionResult) {
        if (expressionResult == null) {
            return "";
        }

        String cacheName = String.valueOf(expressionResult);
        return cacheName;
    }

    private void initializeCache() {
        StandardCache<String, List<String>> sc = new StandardCache<String, List<String>>(CACHE_NAME, false, 10, 100,
                null, log);
        this.cache = sc;
    }

    public void put(final String cacheName, final TemplateMode templateMode, final Locale locale,
            final List<String> content) {
        getCache().put(getCacheName(cacheName, templateMode, locale), content);
    }

}
