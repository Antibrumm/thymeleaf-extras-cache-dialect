package ch.mfrey.thymeleaf.extras.cache;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

public class ExpressionSupport {

	public static Object getEvaluatedAttributeValue(final Arguments arguments, final String attributeValue) {
		final Configuration configuration = arguments.getConfiguration();
		final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

		final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);

		final Object result = expression.execute(configuration, arguments);
		return result;
	}

	public static String getEvaluatedAttributeValueAsString(final Arguments arguments, final String attributeValue) {
		Object result = getEvaluatedAttributeValue(arguments, attributeValue);
		return (result == null ? "" : result.toString());
	}

}
