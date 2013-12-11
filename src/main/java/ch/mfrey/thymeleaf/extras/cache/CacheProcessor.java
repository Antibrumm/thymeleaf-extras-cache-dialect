package ch.mfrey.thymeleaf.extras.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Macro;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

/**
 * The class responsible for replacing the element by a cached version if such
 * content is found in the cache. Resolves the attribute value to get the final
 * cache name to be able to dynamically generate the cache name if desired.
 * 
 * Supports an additional "cache:ttl=''" attribute to define the time-to-live of
 * the cached content in seconds. TTL is not extended on a cache hit.
 * 
 * If no cached content is found yet this processor adds an additional
 * div-element into the current element. This new element the CacheAddProcessor
 * will use as a trigger to finally generate the content string to be put into
 * the cache.
 * 
 * @author Martin Frey
 * 
 */
public class CacheProcessor extends AbstractAttrProcessor {

	private static final String CACHE_TTL = "cache:ttl";

	public static final Logger log = LoggerFactory.getLogger(CacheProcessor.class);
	public static final int PRECEDENCE = 10;

	public CacheProcessor() {
		super("name");
	}

	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
		final String attributeValue = element.getAttributeValue(attributeName);
		element.removeAttribute(attributeName);

		final String cacheName = ExpressionSupport.getEvaluatedAttributeValueAsString(arguments, attributeValue);
		if (cacheName == "") {
			log.debug("Cache name not resolvable: {}", attributeValue);
			return ProcessorResult.OK;
		}

		int cacheTTLs = 0;
		try {
			String ttlValue = element.getAttributeValue(CACHE_TTL);
			if (ttlValue != null) {
				ttlValue = ExpressionSupport.getEvaluatedAttributeValueAsString(arguments, ttlValue);
				if (ttlValue == "") {
					ttlValue = null;
				} else {
					cacheTTLs = Integer.parseInt(ttlValue);
				}
				element.removeAttribute(CACHE_TTL);
			}
		} catch (NumberFormatException e) {
			log.warn("cache:ttl defined but not parseable");
		}

		List<Node> contents = CacheManager.get(arguments, cacheName, cacheTTLs);

		if (contents != null && contents.size() == 1 && contents.get(0) instanceof Macro) {
			log.debug("Cache found. Replacing element.");
			// The object is the cached string representation
			element.clearChildren();
			element.getParent().insertAfter(element, contents.get(0));
			element.getParent().removeChild(element);
		} else {
			log.debug("Cache not found. Adding add processor element.");
			Element cacheDiv = new Element("div");
			cacheDiv.setAttribute("cache:add", cacheName);
			element.addChild(cacheDiv);
		}
		return ProcessorResult.OK;
	}

	@Override
	public int getPrecedence() {
		return PRECEDENCE;
	}

}
