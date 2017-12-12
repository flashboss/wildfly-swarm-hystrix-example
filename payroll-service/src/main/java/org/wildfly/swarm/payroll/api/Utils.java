package org.wildfly.swarm.payroll.api;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

public class Utils {
	private static final String EMPLOYEE_ENDPOINT_KEY = "EMPLOYEE_ENDPOINT";

	public static String getEmployeeEndpoint(String resourcePath) {
		String baseUrl = "";

		if (!isEmpty(getenv("EMPLOYEE_APP_SERVICE_HOST")) // check kubernetes service
				&& !isEmpty(getenv("EMPLOYEE_APP_SERVICE_PORT")))
			baseUrl = "http://" + getenv("EMPLOYEE_APP_SERVICE_HOST") + ":" + getenv("EMPLOYEE_APP_SERVICE_PORT");

		if (isEmpty(baseUrl)) { // check system properties
			baseUrl = getProperty(EMPLOYEE_ENDPOINT_KEY);
		}

		if (isEmpty(baseUrl)) { // check environment variables
			baseUrl = getenv(EMPLOYEE_ENDPOINT_KEY);
		}

		if (isEmpty(baseUrl)) { // default value
			baseUrl = "http://localhost:8080";
		}

		return baseUrl + resourcePath;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
}
