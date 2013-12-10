package ch.mfrey.thymeleaf.extras.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

public class CacheProcessor extends AbstractAttrProcessor {
	public static final Logger log = LoggerFactory.getLogger(CacheProcessor.class);

	public CacheProcessor() {
		super("name");
	}

	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
		log.debug("Checking Cache");
		String cacheName = element.getAttributeValue(attributeName);
		Object object = arguments.getTemplateEngine().getCacheManager().getFragmentCache()
				.get(CacheDialect.CACHE_PREFIX + cacheName);
		element.removeAttribute(attributeName);
		if (object != null) {
			log.debug("Cache found. Replacing");
			@SuppressWarnings("unchecked")
			List<Node> children = (List<Node>) object;
			element.clearChildren();
			element.setChildren(children);
		} else {
			log.debug("Cache not found. Adding add element");
			Element cacheDiv = new Element("div");
			cacheDiv.setAttribute("cache:add", cacheName);
			element.addChild(cacheDiv);
		}
		element.setRecomputeProcessorsImmediately(true);
		return ProcessorResult.OK;
	}

	@Override
	public int getPrecedence() {
		return 10;
	}

}
