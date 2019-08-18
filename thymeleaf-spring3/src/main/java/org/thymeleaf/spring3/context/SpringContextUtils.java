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
package org.thymeleaf.spring3.context;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.spring3.expression.IThymeleafEvaluationContext;
import org.thymeleaf.spring3.expression.ThymeleafEvaluationContext;

/**
 * <p>
 *   Utility class for easy access of information stored at the context in a Spring-enabled application
 *   (such as the Spring ApplicationContext).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public class SpringContextUtils {


    /**
     * <p>
     *   Get the {@link ApplicationContext} from the Thymeleaf template context.
     * </p>
     * <p>
     *   Note that the application context might not be always accessible (and thus this method
     *   can return {@code null}). Application Context will be accessible when the template is being executed
     *   as a Spring View, or else when an object of class {@link ThymeleafEvaluationContext} has been
     *   explicitly set into the {@link ITemplateContext} {@code context} with variable name
     *   {@link ThymeleafEvaluationContext#THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME}.
     * </p>
     *
     * @param context the template context.
     * @return the application context, or {@code null} if it could not be accessed.
     */
    public static ApplicationContext getApplicationContext(final ITemplateContext context) {
        if (context == null) {
            return null;
        }
        // The ThymeleafEvaluationContext is set into the model by ThymeleafView (or wrapped by the SPEL evaluator)
        final IThymeleafEvaluationContext evaluationContext =
                (IThymeleafEvaluationContext) context.getVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);
        if (evaluationContext == null || !(evaluationContext instanceof ThymeleafEvaluationContext)) {
            return null;
        }
        // Only when the evaluation context is a ThymeleafEvaluationContext we can access the ApplicationContext.
        // The reason is it could also be a wrapper on another EvaluationContext implementation, created at the
        // SPELVariableExpressionEvaluator on-the-fly (where ApplicationContext is not available because there might
        // even not exist one), instead of at ThymeleafView (where we are sure we are executing a Spring View and
        // have an ApplicationContext available).
        return ((ThymeleafEvaluationContext)evaluationContext).getApplicationContext();
    }



    private SpringContextUtils() {
        super();
    }


}
