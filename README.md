
Thymeleaf Cache Dialect
========================

A dialect for Thymeleaf that allows you to do partial page caching.

Some parts of our webpage will never change during the lifetime of the application or a usersession, but the part should still be dynamic in the beginning. 

!This implementation is far from optimized yet!

 - Current version: 0.0.1-SNAPSHOT
 - Released: 07 November 2013


Requirements
------------

 - Java 5
 - Thymeleaf 2.1.0+ (2.1.0.RELEASE and its dependencies included)


Installation
------------

### For Maven and Maven-compatible dependency managers
Add a dependency to your project with the following co-ordinates:

 - GroupId: `ch.mfrey.thymeleaf.extras.cache`
 - ArtifactId: `thymeleaf-cache-dialect`
 - Version: `0.0.1-SNAPSHOT`

	<dependency>
		<groupId>ch.mfrey.thymeleaf.extras.cache</groupId>
		<artifactId>thymeleaf-cache-dialect</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>

Usage
-----

Add the Cache dialect to your existing Thymeleaf template engine, eg:

```java
ServletContextTemplateResolver templateresolver = new ServletContextTemplateResolver();
templateresolver.setTemplateMode("HTML5");

templateengine = new TemplateEngine();
templateengine.setTemplateResolver(templateresolver);
templateengine.addDialect(new CacheDialect());		// This line adds the dialect to Thymeleaf
```

Or, for those using Spring configuration files:

```xml
<bean id="templateResolver" class="org.thymeleaf.templateresolver.ServletContextTemplateResolver">
  <property name="templateMode" value="HTML5"/>
</bean>

<bean id="templateEngine" class="org.thymeleaf.spring3.SpringTemplateEngine">
  <property name="templateResolver" ref="templateResolver"/>

  <!-- These lines add the dialect to Thymeleaf -->
  <property name="additionalDialects">
    <set>
      <beans:bean class="ch.mfrey.thymeleaf.extras.cache.CacheDialect" />
    </set>
  </property>

</bean>
```

Use it
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
	xmlns:with="http://www.thymeleaf.org/extras/with">
<head></head>
<body>
	<ul cache:name="menu+${someVar}">
		<li th:text="${a.title}"></li>
		<li th:each="b : ${a.bs}" th:object="${b}">
			<span th:text="*{text}"></span>
			<ul>
				<li th:each="c : *{cs}" th:object="${c}">
					<span th:text="*{text}"></span>
				</li>
			</ul>
		</li>
	</ul>
</body>
</html>
```


Changelog
---------

### 0.0.1-SNAPSHOT
 - Initial commit.