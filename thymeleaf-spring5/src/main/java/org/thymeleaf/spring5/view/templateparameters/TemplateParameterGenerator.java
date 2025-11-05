package org.thymeleaf.spring5.view.templateparameters;

import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

public interface TemplateParameterGenerator {
    Map<String, Object> generateParameters(HttpServletRequest request, RequestContext requestContext, Locale templateLocale, String templateContentType, String templateCharacterEncoding, String templateName);
}
