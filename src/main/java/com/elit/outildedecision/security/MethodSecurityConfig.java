
package com.elit.outildedecision.security;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Configuration class for method-level security.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = false)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

  /** The bean factory used to create method security expression handlers. */
  private BeanFactory beanFactory;

  /**
   * Creates a custom method security expression handler that uses the configured bean factory.
   *
   * @return the custom method security expression handler
   */
  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    CustomMethodSecurityExpressionHandler expressionHandler =
      new CustomMethodSecurityExpressionHandler();
    expressionHandler.setBeanFactory(beanFactory);
    return expressionHandler;
  }

  /**
   * Sets the bean factory used by this configuration class.
   *
   * @param beanFactory the bean factory to set
   * @throws BeansException if an error occurs while setting the bean factory
   */
  @Override
  public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
    super.setBeanFactory(beanFactory);
    this.beanFactory = beanFactory;
  }
}

