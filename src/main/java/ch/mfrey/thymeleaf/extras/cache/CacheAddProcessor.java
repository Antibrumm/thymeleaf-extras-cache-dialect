package ch.mfrey.thymeleaf.extras.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

public class CacheAddProcessor extends AbstractAttrProcessor {
	public static final Logger log = LoggerFactory.getLogger(CacheAddProcessor.class);

	public CacheAddProcessor() {
		super("add");
	}

	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
		String cacheName = element.getAttributeValue(attributeName);

		log.debug("Caching element {}", cacheName);

		NestableNode parent = element.getParent();
		parent.removeChild(element);
		List<Node> children = parent.getChildren();
		for (Node child : children) {
			child.setProcessable(false);
		}

		arguments.getTemplateEngine().getCacheManager().getFragmentCache()
				.put(CacheDialect.CACHE_PREFIX + cacheName, children);
		element.removeAttribute(attributeName);
		return ProcessorResult.OK;
	}

	@Override
	public int getPrecedence() {
		return 10;
	}

}
