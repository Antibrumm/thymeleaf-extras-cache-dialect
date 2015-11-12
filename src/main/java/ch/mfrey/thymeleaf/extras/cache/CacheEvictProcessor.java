package ch.mfrey.thymeleaf.extras.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class CacheEvictProcessor extends AbstractStandardExpressionAttributeTagProcessor {

    public static final Logger log = LoggerFactory.getLogger(CacheEvictProcessor.class);
    public static final int PRECEDENCE = 9;

    protected CacheEvictProcessor(IProcessorDialect dialect, TemplateMode templateMode, String dialectPrefix) {
        super(dialect, templateMode, dialectPrefix, "evict", PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, String attributeTemplateName, int attributeLine,
            int attributeCol, Object expressionResult, IElementTagStructureHandler structureHandler) {
        String cacheName = CacheManager.INSTANCE.getCacheNameFromExpressionResult(expressionResult);
        if (cacheName == "") {
            log.debug("Cache eviction name resulted in empty string - ignoring {}", attributeValue);
            return;
        }

        log.debug("Cache eviction for {}", cacheName);
        CacheManager.INSTANCE.evict(cacheName, getTemplateMode(), context.getLocale());
    }

}
