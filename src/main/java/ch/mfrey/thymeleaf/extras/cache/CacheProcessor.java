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

public class CacheProcessor extends AbstractAttrProcessor {
	public static final Logger log = LoggerFactory.getLogger(CacheProcessor.class);
	public static final int PRECEDENCE = 10;

	public CacheProcessor() {
		super("name");
	}

	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
		log.debug("Checking Cache");
		final String attributeValue = element.getAttributeValue(attributeName);

		final String result = ExpressionSupport.getEvaluatedAttributeValueAsString(arguments, attributeValue);
		if (result == "") {
			log.debug("Cache name not resolvable: {}", attributeValue);
			return ProcessorResult.OK;
		}

		String cacheName = result.toString();

		List<Node> contents = CacheManager.get(arguments, cacheName);
		element.removeAttribute(attributeName);
		if (contents != null && contents.size() == 1 && contents.get(0) instanceof Macro) {
			log.debug("Cache found. Replacing");
			// The object is the cached string representation
			element.clearChildren();
			element.getParent().insertAfter(element, contents.get(0));
			element.getParent().removeChild(element);
		} else {
			log.debug("Cache not found. Adding add element");
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
