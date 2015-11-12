package ch.mfrey.thymeleaf.extras.cache;

import java.util.List;

import org.thymeleaf.cache.ICacheEntryValidityChecker;

class TTLCacheValidityChecker implements ICacheEntryValidityChecker<String, List<String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int cacheTTLs;

	public TTLCacheValidityChecker(int cacheTTLs) {
		super();
		this.cacheTTLs = cacheTTLs;
	}

	public boolean checkIsValueStillValid(String key, List<String> value, long entryCreationTimestamp) {
		final long currentTimeInMillis = System.currentTimeMillis();
		return (currentTimeInMillis < entryCreationTimestamp + this.cacheTTLs * 1000);
	}

}