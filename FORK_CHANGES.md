# Fork Changes - Template Parameter Generator

## Overview

This fork adds an extensible mechanism for dynamically injecting parameters into Thymeleaf template rendering through the `TemplateParameterGenerator` interface.

## Motivation

When rendering Thymeleaf views in Spring applications, there are scenarios where you need to inject additional parameters into templates based on:
- Request context
- Template metadata (name, locale, content type)
- Custom business logic
- Security context
- Feature flags

Previously, these parameters had to be added manually to the model in each controller or through interceptors. The `TemplateParameterGenerator` interface provides a clean, reusable extension point for this common pattern.

## Implementation

### Spring 5

**Interface Location:** `org.thymeleaf.spring5.view.templateparameters.TemplateParameterGenerator`

```java
public interface TemplateParameterGenerator {
    Map<String, Object> generateParameters(
        HttpServletRequest request,
        RequestContext requestContext,
        Locale templateLocale,
        String templateContentType,
        String templateCharacterEncoding,
        String templateName
    );
}
```

### Spring 6

**Interface Location:** `org.thymeleaf.spring6.view.TemplateParameterGenerator`

```java
public interface TemplateParameterGenerator {
    Map<String, Object> generateParameters(
        HttpServletRequest request,
        RequestContext requestContext,
        Locale templateLocale,
        String templateContentType,
        String templateCharacterEncoding,
        String templateName
    );
}
```

## Usage

### 1. Create a Parameter Generator

Implement the `TemplateParameterGenerator` interface and register it as a Spring bean:

```java
@Component
public class CustomParameterGenerator implements TemplateParameterGenerator {

    @Override
    public Map<String, Object> generateParameters(
            HttpServletRequest request,
            RequestContext requestContext,
            Locale templateLocale,
            String templateContentType,
            String templateCharacterEncoding,
            String templateName) {

        Map<String, Object> parameters = new HashMap<>();

        // Add custom parameters based on template name
        if (templateName.startsWith("admin/")) {
            parameters.put("isAdminTemplate", true);
            parameters.put("adminUser", getAdminUser(request));
        }

        // Add locale-specific parameters
        parameters.put("currentLocale", templateLocale.toString());

        return parameters;
    }
}
```

### 2. Multiple Parameter Generators

You can define multiple parameter generators. They will all be automatically discovered and invoked:

```java
@Component
public class SecurityParameterGenerator implements TemplateParameterGenerator {
    @Autowired
    private SecurityService securityService;

    @Override
    public Map<String, Object> generateParameters(...) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("currentUser", securityService.getCurrentUser());
        parameters.put("permissions", securityService.getUserPermissions());
        return parameters;
    }
}

@Component
public class FeatureFlagParameterGenerator implements TemplateParameterGenerator {
    @Autowired
    private FeatureFlagService featureFlagService;

    @Override
    public Map<String, Object> generateParameters(...) {
        return featureFlagService.getAllFlags();
    }
}
```

### 3. Use in Templates

The generated parameters are available in your Thymeleaf templates:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>My Page</title>
</head>
<body>
    <div th:if="${isAdminTemplate}">
        <p>Welcome, <span th:text="${adminUser.name}">Admin</span></p>
    </div>

    <div>Current locale: <span th:text="${currentLocale}">en_US</span></div>

    <div th:if="${featureXEnabled}">
        <!-- Feature X content -->
    </div>
</body>
</html>
```

## Technical Details

### Integration Points

The `TemplateParameterGenerator` beans are autowired into `ThymeleafView`:

```java
@Autowired(required = false)
private List<TemplateParameterGenerator> parameterGenerators;
```

During template rendering, if parameter generators are present, they are invoked and their results are merged:

```java
if (parameterGenerators != null) {
    Map<String, Object> params = generateTemplateRenderingParameters(...);
    viewTemplateEngine.process(
        new TemplateSpec(templateName, processMarkupSelectors, null, params),
        context,
        response.getWriter()
    );
}
```

### Parameter Merging

If multiple generators return the same parameter key, the last generator's value wins (using `(first, second) -> second` merge function).

### Null Handling

Generators can return `null`, which will be filtered out. Individual parameters cannot be `null` (as they're added to a Map).

## Benefits

1. **Separation of Concerns**: Move parameter injection logic out of controllers
2. **Reusability**: Define once, use across all templates
3. **Testability**: Easy to unit test parameter generation logic
4. **Extensibility**: Add new generators without modifying existing code
5. **Context-Aware**: Access to full request context for dynamic parameter generation

## Compatibility

- **Spring 5**: Uses `javax.servlet` APIs
- **Spring 6**: Uses `jakarta.servlet` APIs
- **Thymeleaf**: 3.1.x compatible
- **Backward Compatible**: If no generators are defined, behavior is identical to upstream Thymeleaf

## Changes Made

### Added Files
- `thymeleaf-spring5/src/main/java/org/thymeleaf/spring5/view/templateparameters/TemplateParameterGenerator.java`
- `thymeleaf-spring6/src/main/java/org/thymeleaf/spring6/view/TemplateParameterGenerator.java`

### Modified Files
- `thymeleaf-spring5/src/main/java/org/thymeleaf/spring5/view/ThymeleafView.java`
  - Added `@Autowired List<TemplateParameterGenerator>` field
  - Added `generateTemplateRenderingParameters()` method
  - Modified `renderFragment()` to use generators when present

- `thymeleaf-spring6/src/main/java/org/thymeleaf/spring6/view/ThymeleafView.java`
  - Added `@Autowired List<TemplateParameterGenerator>` field
  - Added `generateTemplateRenderingParameters()` method
  - Modified `renderFragment()` to use generators when present

### Additional Changes
- Updated method names from "blacklist/whitelist" to "blocked/allowed" terminology for inclusivity
- Changed Maven group ID to `uk.co.kedos.thymeleaf` for fork distribution
- Updated version to `3.1.1.RELEASE`
