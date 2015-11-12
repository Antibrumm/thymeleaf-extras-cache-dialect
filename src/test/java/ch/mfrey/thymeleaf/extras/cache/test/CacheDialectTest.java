package ch.mfrey.thymeleaf.extras.cache.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import ch.mfrey.thymeleaf.extras.cache.CacheDialect;

public class CacheDialectTest {

    public static class A {
        private String text;
        private List<A> as;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<A> getAs() {
            if (as == null) {
                as = new ArrayList<A>();
            }
            return as;
        }

        public void setAs(List<A> as) {
            this.as = as;
        }

        public String getLongCalc() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return "2 sec";
            }
            return "1 sec";
        }
    }

    private TemplateEngine templateEngine;

    @Before
    public void setUpTemplateEngine() {
        ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setCacheable(false);
        classLoaderTemplateResolver.setTemplateMode("HTML5");
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(classLoaderTemplateResolver);
        templateEngine.addDialect(new CacheDialect());
    }

    private A buildRecursive(int lvl) {
        A a = new A();
        a.setText("Text " + lvl);
        if (lvl >= 0) {
            for (int i = 0; i < 3; i++) {
                a.getAs().add(buildRecursive(lvl - 1));
            }
        }
        return a;
    }

    private static final Logger log = LoggerFactory.getLogger(CacheDialectTest.class);

    @Test
    public void testCache() throws InterruptedException {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("user_role", "ROLE_USER");
        variables.put("a", buildRecursive(3));
        // Just initializing
        String expected = templateEngine.process("templates/expected.html", new Context(Locale.getDefault(), variables));

        log.debug("FIRST");
        String result = templateEngine.process("templates/cacheTest.html", new Context(Locale.getDefault(), variables));
        Assert.assertEquals(expected, result);

        log.debug("CACHED");
        String resultCached = templateEngine.process("templates/cacheTest.html", new Context(Locale.getDefault(), variables));

        log.debug("TTL");
        Thread.sleep(1500);
        String resultTTL = templateEngine.process("templates/cacheTest.html", new Context(Locale.getDefault(), variables));

        log.debug("EVICT");
        variables.put("mayResolveIntoACachedName", "longCalcValue");
        String resultEvictCache = templateEngine.process("templates/cacheTest.html", new Context(Locale.getDefault(), variables));

        log.debug("SECOND");
        variables.remove("mayResolveIntoACachedName");
        String resultSecond = templateEngine.process("templates/cacheTest.html", new Context(Locale.getDefault(), variables));

        Assert.assertEquals(result, resultCached);
        Assert.assertEquals(result, resultTTL);
        Assert.assertNotEquals(result, resultEvictCache);
        Assert.assertNotEquals(resultEvictCache, resultSecond);

        Assert.assertTrue(result.contains("first"));
        Assert.assertFalse(result.contains("second"));

        Assert.assertTrue(resultEvictCache.contains("first"));
        Assert.assertTrue(resultEvictCache.contains("second"));

        Assert.assertFalse(resultSecond.contains("first"));
        Assert.assertTrue(resultSecond.contains("second"));
    }
}
