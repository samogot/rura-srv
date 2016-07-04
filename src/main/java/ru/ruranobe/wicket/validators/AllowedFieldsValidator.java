package ru.ruranobe.wicket.validators;

import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;

import java.util.List;
import java.util.regex.Pattern;

public class AllowedFieldsValidator extends PatternValidator
{

    public AllowedFieldsValidator(List<String> allowedFields, String delimiter, String paramName)
    {
        super(String.format("^(%1$s)(%2$s(%1$s))*$", Strings.join("|", allowedFields), Pattern.quote(delimiter)));
        this.allowedFields = allowedFields;
        this.delimiter = delimiter;
        this.paramName = paramName;
    }

    public AllowedFieldsValidator(List<String> allowedFields, String delimiter)
    {
        this(allowedFields, delimiter, null);
    }

    public AllowedFieldsValidator(List<String> allowedFields)
    {
        this(allowedFields, "|", null);
    }

    @Override
    protected ValidationError decorate(ValidationError error, IValidatable<String> validatable)
    {
        ValidationError validationError = new ValidationError(this);
        validationError.setVariable("delimiter", delimiter);
        validationError.setVariable("allowed", allowedFields);
        if (paramName != null)
        {
            validationError.setVariable("label", paramName);
        }
        return validationError;
    }

    public List<String> getAllowedFields()
    {
        return allowedFields;
    }

    public AllowedFieldsValidator setAllowedFields(List<String> allowedFields)
    {
        this.allowedFields = allowedFields;
        return this;
    }

    public String getDelimiter()
    {
        return delimiter;
    }

    public AllowedFieldsValidator setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
        return this;
    }

    public String getParamName()
    {
        return paramName;
    }

    public AllowedFieldsValidator setParamName(String paramName)
    {
        this.paramName = paramName;
        return this;
    }

    private List<String> allowedFields;
    private String delimiter;
    private String paramName;
}
