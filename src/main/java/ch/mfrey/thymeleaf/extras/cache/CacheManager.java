package ch.mfrey.thymeleaf.extras.cache;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.dom.Node;

public class CacheManager {
	private static String getCacheName(final String templateMode, final String name) {
		return CacheDialect.CACHE_PREFIX + templateMode + "_" + name;
	}

	private static ICache<String, List<Node>> getCache(final TemplateEngine templateEngine) {
		if (templateEngine == null) {
			throw new RuntimeException("Template Engine cannot be null");
		}
		return templateEngine.getCacheManager().getFragmentCache();
	}

	public static List<Node> get(final Arguments arguments, final String cacheName, final int cacheTTLs) {
		return get(getCache(arguments.getTemplateEngine()), arguments.getTemplateResolution().getTemplateMode(), cacheName,
				cacheTTLs);
	}

	public static List<Node> get(final ICache<String, List<Node>> cache, final String templateMode,
			final String cacheName, final int cacheTTLs) {
		if (cacheTTLs == 0) {
			return cache.get(getCacheName(templateMode, cacheName));
		} else {
			return cache.get(getCacheName(templateMode, cacheName), new TTLCacheValidityChecker(cacheTTLs));
		}
	}

	public static void put(final Arguments arguments, final String cacheName, final List<Node> content) {
		put(getCache(arguments.getTemplateEngine()), arguments.getTemplateResolution().getTemplateMode(), cacheName,
				content);
	}

	public static void put(final ICache<String, List<Node>> cache, final String templateMode, final String cacheName,
			final List<Node> content) {
		cache.put(getCacheName(templateMode, cacheName), content);
	}

	public static void evict(final ICache<String, List<Node>> cache, final String templateMode, final String cacheName) {
		cache.clearKey(getCacheName(templateMode, cacheName));
	}

	public static void evict(final Arguments arguments, final String cacheName) {
		evict(getCache(arguments.getTemplateEngine()), arguments.getTemplateResolution().getTemplateMode(), cacheName);
	}

}
