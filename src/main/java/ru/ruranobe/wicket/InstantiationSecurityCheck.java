package ru.ruranobe.wicket;

import org.apache.wicket.authorization.UnauthorizedInstantiationException;

public interface InstantiationSecurityCheck
{
    /**
     * For simple cases we have AnnotationsRoleAuthorizationStrategy
     * but there can be cases when we have to perform security check
     * in the middle of the component constructor.
     *
     * @throws UnauthorizedInstantiationException if security check failed
     */
    void doInstantiationSecurityCheck();
}
