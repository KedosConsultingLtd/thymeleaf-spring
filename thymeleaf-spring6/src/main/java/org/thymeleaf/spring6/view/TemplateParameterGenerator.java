package org.thymeleaf.spring6.view;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.support.RequestContext;

import java.util.Locale;
import java.util.Map;

public interface TemplateParameterGenerator {
    Map<String, Object> generateParameters(HttpServletRequest request, RequestContext requestContext, Locale templateLocale, String templateContentType, String templateCharacterEncoding, String templateName);
}
