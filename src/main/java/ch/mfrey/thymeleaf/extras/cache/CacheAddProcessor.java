package ch.mfrey.thymeleaf.extras.cache;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

public class CacheAddProcessor extends AbstractAttributeModelProcessor {

    private static final Logger log = LoggerFactory.getLogger(CacheAddProcessor.class);
    public static final int PRECEDENCE = 11;

    protected CacheAddProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, "add", true, PRECEDENCE, false);
    }

    @Override
    protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName, String attributeValue,
            IElementModelStructureHandler structureHandler) {
        final IEngineConfiguration configuration = context.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final Object expressionResult;
        if (attributeValue != null) {
            final IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
            expressionResult = expression.execute(context);
        } else {
            expressionResult = null;
        }

        String cacheName = CacheManager.INSTANCE.getCacheNameFromExpressionResult(expressionResult);
        if (cacheName == "") {
            log.debug("Cache name not resolvable: {}", attributeValue);
            return;
        }

        log.debug("Caching element {}", cacheName);

        try {
            // We have to remove the attribute first before we continue
            // processing
            // -> see constructor uses 'removeAttribute=false'
            IProcessableElementTag firstEvent = (IProcessableElementTag) model.get(0);
            final IModelFactory modelFactory = context.getModelFactory();
            final IProcessableElementTag newFirstEvent = modelFactory.removeAttribute(firstEvent, attributeName);
            if (newFirstEvent != firstEvent) {
                model.replace(0, newFirstEvent);
            }

            final StringWriter modelWriter = new StringWriter();
            model.write(modelWriter);
            final TemplateModel cacheModel = configuration.getTemplateManager()
                    .parseString(context.getTemplateData(), modelWriter.toString(), firstEvent.getLine(),
                            firstEvent.getCol(),
                            getTemplateMode(), false);

            final StringWriter templateWriter = new StringWriter();
            configuration.getTemplateManager().process(cacheModel, context, templateWriter);

            CacheManager.INSTANCE.put(cacheName, getTemplateMode(), context.getLocale(),
                    Collections.singletonList(templateWriter.toString()));
        } catch (IOException e) {
            throw new TemplateProcessingException("Error during creation of output", e);
        }
    }

}
