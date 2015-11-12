package ch.mfrey.thymeleaf.extras.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * The class responsible for replacing the element by a cached version if such content is found in the cache. Resolves
 * the attribute value to get the final cache name to be able to dynamically generate the cache name if desired.
 * 
 * Supports an additional "cache:ttl=''" attribute to define the time-to-live of the cached content in seconds. TTL is
 * not extended on a cache hit.
 * 
 * If no cached content is found yet this processor adds an additional div-element into the current element. This new
 * element the CacheAddProcessor will use as a trigger to finally generate the content string to be put into the cache.
 * 
 * @author Martin Frey
 * 
 */
public class CacheProcessor extends AbstractStandardExpressionAttributeTagProcessor {

    private static final String CACHE_TTL = "cache:ttl";
    public static final Logger log = LoggerFactory.getLogger(CacheProcessor.class);
    public static final int PRECEDENCE = 10;

    protected CacheProcessor(IProcessorDialect dialect, TemplateMode templateMode, String dialectPrefix) {
        super(dialect, templateMode, dialectPrefix, "name", PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, String attributeTemplateName, int attributeLine,
            int attributeCol, Object expressionResult, IElementTagStructureHandler structureHandler) {
        String cacheName = CacheManager.INSTANCE.getCacheNameFromExpressionResult(expressionResult);
        if (cacheName == "") {
            log.debug("Cache name not resolvable: {}", attributeValue);
            return;
        }

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

        final int cacheTTL;
        String ttlValue = tag.getAttributes().getValue(CACHE_TTL);
        if (ttlValue != null) {
            final IStandardExpression expression = expressionParser.parseExpression(context, ttlValue);
            cacheTTL = ((Number) expression.execute(context)).intValue();
        } else {
            cacheTTL = 0;
        }
        tag.getAttributes().removeAttribute(CACHE_TTL);

        List<String> contents = CacheManager.INSTANCE.get(cacheName, getTemplateMode(), context.getLocale(), cacheTTL);

        if (contents != null && contents.size() == 1) {
            log.debug("Cache found {}. Replacing element.", cacheName);
            // The object is the cached string representation
            structureHandler.replaceWith(contents.get(0), false);
        } else {
            log.debug("Cache {} not found. Adding add processor element.", cacheName);
            // structureHandler.insertImmediatelyAfter(null, true);
            tag.getAttributes().setAttribute("cache:add", cacheName);
        }

    }

}
