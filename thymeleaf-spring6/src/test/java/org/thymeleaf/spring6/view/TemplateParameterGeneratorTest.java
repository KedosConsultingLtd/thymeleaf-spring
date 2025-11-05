/*
 * =============================================================================
 *
 *   Copyright (c) 2024, Kedos Consulting Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.thymeleaf.spring6.view;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.support.RequestContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TemplateParameterGenerator interface and its usage.
 */
@ExtendWith(MockitoExtension.class)
class TemplateParameterGeneratorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private RequestContext requestContext;

    @Test
    @DisplayName("Simple parameter generator should return parameters")
    void testSimpleParameterGenerator() {
        TemplateParameterGenerator generator = new SimpleTestParameterGenerator();

        Map<String, Object> params = generator.generateParameters(
                request,
                requestContext,
                Locale.US,
                "text/html",
                "UTF-8",
                "testTemplate"
        );

        assertNotNull(params);
        assertEquals("value1", params.get("param1"));
        assertEquals("value2", params.get("param2"));
    }

    @Test
    @DisplayName("Parameter generator should have access to request context")
    void testParameterGeneratorAccessToRequest() {
        when(request.getParameter("testParam")).thenReturn("testValue");

        TemplateParameterGenerator generator = (req, ctx, locale, contentType, encoding, templateName) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("requestParam", req.getParameter("testParam"));
            return params;
        };

        Map<String, Object> params = generator.generateParameters(
                request,
                requestContext,
                Locale.US,
                "text/html",
                "UTF-8",
                "testTemplate"
        );

        assertEquals("testValue", params.get("requestParam"));
    }

    @Test
    @DisplayName("Parameter generator should have access to locale")
    void testParameterGeneratorAccessToLocale() {
        TemplateParameterGenerator generator = (req, ctx, locale, contentType, encoding, templateName) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("currentLocale", locale.toString());
            params.put("language", locale.getLanguage());
            return params;
        };

        Map<String, Object> params = generator.generateParameters(
                request,
                requestContext,
                Locale.FRANCE,
                "text/html",
                "UTF-8",
                "testTemplate"
        );

        assertEquals("fr_FR", params.get("currentLocale"));
        assertEquals("fr", params.get("language"));
    }

    @Test
    @DisplayName("Parameter generator should have access to template metadata")
    void testParameterGeneratorAccessToTemplateMetadata() {
        TemplateParameterGenerator generator = (req, ctx, locale, contentType, encoding, templateName) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("templateName", templateName);
            params.put("contentType", contentType);
            params.put("encoding", encoding);
            return params;
        };

        Map<String, Object> params = generator.generateParameters(
                request,
                requestContext,
                Locale.US,
                "text/html",
                "UTF-8",
                "admin/dashboard"
        );

        assertEquals("admin/dashboard", params.get("templateName"));
        assertEquals("text/html", params.get("contentType"));
        assertEquals("UTF-8", params.get("encoding"));
    }

    @Test
    @DisplayName("Parameter generator can return null")
    void testParameterGeneratorCanReturnNull() {
        TemplateParameterGenerator generator = (req, ctx, locale, contentType, encoding, templateName) -> null;

        Map<String, Object> params = generator.generateParameters(
                request,
                requestContext,
                Locale.US,
                "text/html",
                "UTF-8",
                "testTemplate"
        );

        assertNull(params);
    }

    @Test
    @DisplayName("Parameter generator can return empty map")
    void testParameterGeneratorCanReturnEmptyMap() {
        TemplateParameterGenerator generator = (req, ctx, locale, contentType, encoding, templateName) -> new HashMap<>();

        Map<String, Object> params = generator.generateParameters(
                request,
                requestContext,
                Locale.US,
                "text/html",
                "UTF-8",
                "testTemplate"
        );

        assertNotNull(params);
        assertTrue(params.isEmpty());
    }

    @Test
    @DisplayName("Parameter generator can use template name for conditional logic")
    void testConditionalParameterGenerationBasedOnTemplate() {
        TemplateParameterGenerator generator = (req, ctx, locale, contentType, encoding, templateName) -> {
            Map<String, Object> params = new HashMap<>();

            if (templateName.startsWith("admin/")) {
                params.put("isAdminTemplate", true);
                params.put("requiredRole", "ADMIN");
            } else {
                params.put("isAdminTemplate", false);
                params.put("requiredRole", "USER");
            }

            return params;
        };

        Map<String, Object> adminParams = generator.generateParameters(
                request, requestContext, Locale.US, "text/html", "UTF-8", "admin/users"
        );
        assertTrue((Boolean) adminParams.get("isAdminTemplate"));
        assertEquals("ADMIN", adminParams.get("requiredRole"));

        Map<String, Object> userParams = generator.generateParameters(
                request, requestContext, Locale.US, "text/html", "UTF-8", "public/home"
        );
        assertFalse((Boolean) userParams.get("isAdminTemplate"));
        assertEquals("USER", userParams.get("requiredRole"));
    }

    /**
     * Simple test implementation of TemplateParameterGenerator
     */
    static class SimpleTestParameterGenerator implements TemplateParameterGenerator {
        @Override
        public Map<String, Object> generateParameters(
                HttpServletRequest request,
                RequestContext requestContext,
                Locale templateLocale,
                String templateContentType,
                String templateCharacterEncoding,
                String templateName) {

            Map<String, Object> params = new HashMap<>();
            params.put("param1", "value1");
            params.put("param2", "value2");
            return params;
        }
    }
}
