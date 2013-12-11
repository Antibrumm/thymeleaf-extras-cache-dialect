package ch.mfrey.thymeleaf.extras.cache;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Macro;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateOutputException;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templatewriter.AbstractGeneralTemplateWriter;
import org.thymeleaf.templatewriter.ITemplateWriter;

public class CacheAddProcessor extends AbstractAttrProcessor {
	public static final Logger log = LoggerFactory.getLogger(CacheAddProcessor.class);

	public CacheAddProcessor() {
		super("add");
	}

	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
		String cacheName = element.getAttributeValue(attributeName);
		log.debug("Caching element {}", cacheName);

		String templateMode = arguments.getTemplateResolution().getTemplateMode();

		final ITemplateModeHandler templateModeHandler = arguments.getConfiguration().getTemplateModeHandler(templateMode);
		final ITemplateWriter templateWriter = templateModeHandler.getTemplateWriter();

		if (templateWriter == null) {
			throw new ConfigurationException("No template writer defined for template mode \"" + templateMode + "\"");
		} else if (!AbstractGeneralTemplateWriter.class.isAssignableFrom(templateWriter.getClass())) {
			throw new ConfigurationException("The template writer defined for template mode \"" + templateMode
					+ "\" is not an AbstractGeneralTemplateWriter");
		}

		StringWriter writer = new StringWriter();
		try {
			NestableNode parent = element.getParent();
			parent.removeChild(element);
			((AbstractGeneralTemplateWriter) templateWriter).writeNode(arguments, writer, parent);

			Node content = new Macro(writer.toString());
			CacheManager.put(arguments, cacheName, Collections.singletonList(content));
		} catch (IOException e) {
			throw new TemplateOutputException("Error during creation of output", e);
		}
		return ProcessorResult.OK;
	}

	@Override
	public int getPrecedence() {
		return 10;
	}

}
