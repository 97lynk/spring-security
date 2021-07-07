//package io.a97lynk.formlogin.config;
//
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import java.io.IOException;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class XSSFilter implements Filter {
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response,
//	                     FilterChain chain) throws IOException, ServletException {
//		XSSRequestWrapper wrappedRequest =
//				new XSSRequestWrapper((HttpServletRequest) request);
//		chain.doFilter(wrappedRequest, response);
//	}
//
//	// other methods
//
//	public class XSSRequestWrapper extends HttpServletRequestWrapper {
//
//		@Override
//		public String[] getParameterValues(String parameter) {
//			String[] values = super.getParameterValues(parameter);
//			if (values == null) {
//				return null;
//			}
//			int count = values.length;
//			String[] encodedValues = new String[count];
//			for (int i = 0; i < count; i++) {
//				encodedValues[i] = stripXSS(values[i]);
//			}
//			return encodedValues;
//		}
//		@Override
//		public String getParameter(String parameter) {
//			String value = super.getParameter(parameter);
//			return stripXSS(value);
//		}
//	}
//}