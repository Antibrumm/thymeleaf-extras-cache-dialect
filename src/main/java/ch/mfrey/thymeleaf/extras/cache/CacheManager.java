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

	public static List<Node> get(final Arguments arguments, final String cacheName) {
		return getCache(arguments.getTemplateEngine()).get(
				getCacheName(arguments.getTemplateResolution().getTemplateMode(), cacheName));
	}

	public static void put(final Arguments arguments, final String cacheName, List<Node> content) {
		getCache(arguments.getTemplateEngine()).put(
				getCacheName(arguments.getTemplateResolution().getTemplateMode(), cacheName), content);
	}
}
