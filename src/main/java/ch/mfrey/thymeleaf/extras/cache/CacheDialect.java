package ch.mfrey.thymeleaf.extras.cache;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

public class CacheDialect extends AbstractDialect {
	public static final String DIALECT_NAMESPACE = "http://www.thymeleaf.org/extras/cache";

	public static final String DIALECT_PREFIX = "cache";

	public static final String CACHE_PREFIX = "CacheDialect_";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<IProcessor> getProcessors() {
		HashSet<IProcessor> processors = new HashSet<IProcessor>();
		processors.add(new CacheProcessor());
		processors.add(new CacheAddProcessor());
		return processors;
	}

	public String getPrefix() {
		return DIALECT_PREFIX;
	}

	public boolean isLenient() {
		return false;
	}

}
