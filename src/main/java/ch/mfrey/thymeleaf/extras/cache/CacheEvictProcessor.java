package ch.mfrey.thymeleaf.extras.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

public class CacheEvictProcessor extends AbstractAttrProcessor {
	public static final Logger log = LoggerFactory.getLogger(CacheEvictProcessor.class);
	public static final int PRECEDENCE = 10;

	public CacheEvictProcessor() {
		super("evict");
	}

	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
		final String attributeValue = element.getAttributeValue(attributeName);

		element.removeAttribute(attributeName);

		final String cacheName = ExpressionSupport.getEvaluatedAttributeValueAsString(arguments, attributeValue);
		if (cacheName == "") {
			return ProcessorResult.OK;
		}

		CacheManager.INSTANCE.evict(arguments, cacheName);

		return ProcessorResult.OK;
	}

	@Override
	public int getPrecedence() {
		return CacheProcessor.PRECEDENCE - 1; // Run just before the CacheProcessor
	}

}
