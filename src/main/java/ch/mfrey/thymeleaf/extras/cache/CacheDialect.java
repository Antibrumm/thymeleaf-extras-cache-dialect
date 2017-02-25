package ch.mfrey.thymeleaf.extras.cache;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class CacheDialect extends AbstractProcessorDialect {

    public static final String DIALECT_NAMESPACE = "http://www.thymeleaf.org/extras/cache";

    public static final String DIALECT_PREFIX = "cache";

    public static final int PROCESSOR_PRECEDENCE = StandardDialect.PROCESSOR_PRECEDENCE;

    public CacheDialect() {
        super(DIALECT_NAMESPACE, DIALECT_PREFIX, PROCESSOR_PRECEDENCE);
    }

    /**
     * {@inheritDoc}
     */
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        HashSet<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new CacheProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new CacheAddProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new CacheEvictProcessor(TemplateMode.HTML, dialectPrefix));
        return processors;
    }

}
