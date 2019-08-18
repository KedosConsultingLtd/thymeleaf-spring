/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring4.requestdata;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.spring4.util.SpringVersionUtils;
import org.thymeleaf.util.ClassLoaderUtils;


/**
 * <p>
 *   Utility class used for applying the {@code org.springframework.web.servlet.support.RequestDataValueProcessor}
 *   interface to URLs and forms output by Thymeleaf.
 * </p>
 * <p>
 *   This Spring interface only exists since Spring 3.1, but was modified in Spring 4. This class will only
 *   apply this value processor if the version of Spring is 4 or newer.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
public final class RequestDataValueProcessorUtils {

    private static final boolean canApply;

    private static final String SPRING4_DELEGATE_CLASS =
            RequestDataValueProcessorUtils.class.getPackage().getName() + ".RequestDataValueProcessor4Delegate";
    private static final IRequestDataValueProcessorDelegate spring4Delegate;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDataValueProcessorUtils.class);


    static {

        canApply = SpringVersionUtils.isSpring40AtLeast();

        if (canApply) {
            try {
                final Class<?> implClass = ClassLoaderUtils.loadClass(SPRING4_DELEGATE_CLASS);
                spring4Delegate = (IRequestDataValueProcessorDelegate) implClass.newInstance();
            } catch (final Exception e) {
                throw new ExceptionInInitializerError(
                        new ConfigurationException(
                            "Environment has been detected to be at least Spring 4, but thymeleaf could not initialize a " +
                            "delegate of class \"" + SPRING4_DELEGATE_CLASS + "\"", e));
            }
        } else {
            LOGGER.warn(
                    "[THYMELEAF] You seem to be using the thymeleaf-spring4 module with a Spring version prior to 4.0. " +
                    "Even though most features should work OK, support for CSRF protection on websites will be " +
                    "disabled due to incompatibilities between the different versions of the " +
                    "RequestDataValueProcessor interface in versions 4.x and 3.x.");
            spring4Delegate = null;
        }

    }




    public static String processAction(
            final ITemplateContext context, final String action, final String httpMethod) {

        if (!canApply || !(context instanceof IWebContext)) {
            return action;
        }

        final RequestContext requestContext =
                (RequestContext) context.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return action;
        }

        if (spring4Delegate != null) {
            return spring4Delegate.processAction(requestContext, ((IWebContext)context).getRequest(), action, httpMethod);
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }



    public static String processFormFieldValue(
            final ITemplateContext context, final String name, final String value, final String type) {

        if (!canApply || !(context instanceof IWebContext)) {
            return value;
        }

        final RequestContext requestContext =
                (RequestContext) context.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return value;
        }

        if (spring4Delegate != null) {
            return spring4Delegate.processFormFieldValue(requestContext, ((IWebContext)context).getRequest(), name, value, type);
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }



    public static Map<String, String> getExtraHiddenFields(final ITemplateContext context) {

        if (!canApply || !(context instanceof IWebContext)) {
            return null;
        }

        final RequestContext requestContext =
                (RequestContext) context.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return null;
        }

        if (spring4Delegate != null) {
            return spring4Delegate.getExtraHiddenFields(requestContext, ((IWebContext)context).getRequest());
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }



    public static String processUrl(final ITemplateContext context, final String url) {

        if (!canApply || !(context instanceof IWebContext)) {
            return url;
        }

        final RequestContext requestContext =
                (RequestContext) context.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return url;
        }

        if (spring4Delegate != null) {
            return spring4Delegate.processUrl(requestContext, ((IWebContext)context).getRequest(), url);
        }

        throw new TemplateProcessingException(
                "According to the detected Spring version info, a RequestDataValueProcessor delegate should be available, " +
                "but none seem applicable");

    }




    private RequestDataValueProcessorUtils() {
	    super();
    }



    public static void main(String[] args) {
        System.out.println(SPRING4_DELEGATE_CLASS);
    }

}
