
package com.elit.outildedecision.security;

import com.elit.outildedecision.service.UserService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

/**
 * This class extends the DefaultMethodSecurityExpressionHandler and overrides the createSecurityExpressionRoot()
 * method to return a CustomMethodSecurityExpressionRoot object with the required dependencies and the current
 * authentication object. It also sets the BeanFactory for the class.
 */
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

  private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
  private BeanFactory beanFactory;

  /**
   * Overrides the method from the superclass to return a CustomMethodSecurityExpressionRoot object.
   *
   * @param authentication The authentication object
   * @param invocation The method invocation object
   * @return A CustomMethodSecurityExpressionRoot object with the required dependencies and the current authentication
   *         object
   */
  @Override
  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(final Authentication authentication,
                                                                            final MethodInvocation invocation) {
    CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
    root.setThis(invocation.getThis());
    root.setPermissionEvaluator(getPermissionEvaluator());
    root.setTrustResolver(this.trustResolver);
    root.setRoleHierarchy(getRoleHierarchy());
    root.setUserService(beanFactory.getBean(UserService.class));
    return root;
  }

  /**
   * Sets the BeanFactory for the class.
   *
   * @param beanFactory The BeanFactory object
   */
  public void setBeanFactory(final BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }
}
