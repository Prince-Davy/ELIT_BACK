
package com.elit.outildedecision.security;

import com.elit.outildedecision.service.UserService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import java.util.function.Supplier;

/**
 * This class extends the DefaultMethodSecurityExpressionHandler and overrides the createSecurityExpressionRoot()
 * method to return a CustomMethodSecurityExpressionRoot object with the required dependencies and the current
 * authentication object. It also sets the BeanFactory for the class.
 */

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

  private final UserService userService;

  /**
   * Construit le {@code CustomMethodSecurityExpressionHandler} avec les dépendances requises.
   *
   * @param userService le service utilisateur injecté, utilisé par la root des expressions
   */
  public CustomMethodSecurityExpressionHandler(final UserService userService) {
    this.userService = userService;
  }

  /**
   * Override indispensable en Spring Security 6 : on construit explicitement le contexte
   * avec une root custom et on y attache les paramètres de la méthode comme variables.
   *
   * @param authentication fournisseur de l'authentication courante (lazy)
   * @param mi invocation de méthode AOP contenant la cible et les arguments
   * @return un {@link EvaluationContext} initialisé avec la root custom et les variables de paramètres
   */
  @Override
  public EvaluationContext createEvaluationContext(
    final Supplier<Authentication> authentication,
    final MethodInvocation mi) {

    // 1) Construire la root custom
    final CustomMethodSecurityExpressionRoot root =
      new CustomMethodSecurityExpressionRoot(authentication.get());
    root.setUserService(userService);
    root.setThis(mi.getThis());
    root.setPermissionEvaluator(getPermissionEvaluator());
    root.setTrustResolver(new AuthenticationTrustResolverImpl());
    root.setRoleHierarchy(getRoleHierarchy());
    root.setDefaultRolePrefix(getDefaultRolePrefix());

    // 2) Contexte standard basé sur la root
    final StandardEvaluationContext ctx = new StandardEvaluationContext(root);

    // 3) Binder les paramètres (par nom ET index #p0/#a0)
    final ParameterNameDiscoverer pnd = getParameterNameDiscoverer();
    final String[] paramNames = pnd != null ? pnd.getParameterNames(mi.getMethod()) : null;
    final Object[] args = mi.getArguments();

    for (int i = 0; i < args.length; i++) {
      // par nom, si disponible (ex: #projectId)
      if (paramNames != null && paramNames.length > i && paramNames[i] != null) {
        ctx.setVariable(paramNames[i], args[i]);
      }
      // fallback classique Spring : exposer #p0 et #a0
      ctx.setVariable("p" + i, args[i]);
      ctx.setVariable("a" + i, args[i]);
    }

    return ctx;
  }

  /**
   * Crée la root d'expression de sécurité custom utilisée pour l'évaluation des expressions
   * method-level (ex: {@code @PreAuthorize}).
   *
   * @param authentication l'authentication courante
   * @param invocation l'invocation de méthode en cours
   * @return une instance de {@link CustomMethodSecurityExpressionRoot} configurée
   */
  @Override
  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
    final Authentication authentication,
    final MethodInvocation invocation) {

    final CustomMethodSecurityExpressionRoot root =
      new CustomMethodSecurityExpressionRoot(authentication);
    root.setUserService(userService);
    root.setThis(invocation.getThis());
    root.setPermissionEvaluator(getPermissionEvaluator());
    root.setTrustResolver(new AuthenticationTrustResolverImpl());
    root.setRoleHierarchy(getRoleHierarchy());
    root.setDefaultRolePrefix(getDefaultRolePrefix());
    return root;
  }
}
