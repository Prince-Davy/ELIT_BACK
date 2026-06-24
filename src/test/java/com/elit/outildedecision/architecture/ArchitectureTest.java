package com.elit.outildedecision.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Architecture guard-rails for the journey-based migration (plan §10.7).
 *
 * <p>Rule 1 (inter-context isolation) is enforced strictly: it is already satisfied
 * by the migrated {@code ai} and {@code audit} contexts and must stay true as
 * {@code collaboration} and {@code shared} are populated. Rules 2 and 3 are frozen:
 * existing legacy violations are tolerated but no new one is allowed, and the frozen
 * set shrinks as each context reaches the canonical pattern.
 */
class ArchitectureTest {

  private static final String ROOT_PACKAGE = "com.elit.outildedecision";

  private static JavaClasses productionClasses;

  @BeforeAll
  static void importProductionClasses() {
    productionClasses = new ClassFileImporter()
        .withImportOption(new ImportOption.DoNotIncludeTests())
        .importPackages(ROOT_PACKAGE);
  }

  @Test
  void aiContextShouldNotDependOnOtherContexts() {
    ArchRule rule = classes()
        .that().resideInAPackage("..ai..")
        .should().onlyDependOnClassesThat()
        .resideOutsideOfPackages("..audit..", "..collaboration..");

    rule.check(productionClasses);
  }

  @Test
  void auditContextShouldNotDependOnOtherContexts() {
    ArchRule rule = classes()
        .that().resideInAPackage("..audit..")
        .should().onlyDependOnClassesThat()
        .resideOutsideOfPackages("..ai..", "..collaboration..");

    rule.check(productionClasses);
  }

  @Test
  void collaborationContextShouldNotDependOnOtherContexts() {
    // allowEmptyShould: the collaboration context is created from Étape 10 onward;
    // the rule is defined now so it is enforced the moment the package appears.
    ArchRule rule = classes()
        .that().resideInAPackage("..collaboration..")
        .should().onlyDependOnClassesThat()
        .resideOutsideOfPackages("..ai..", "..audit..")
        .allowEmptyShould(true);

    rule.check(productionClasses);
  }

  @Test
  void sharedShouldNotDependOnFeatureContexts() {
    // Rule 4 (plan §10.7): shared must stay a leaf. The shared kernel (..shared.kernel..) is the
    // ONE documented exception (plan §7.4, Étape 20, Option B): it hosts the Project/User root
    // aggregates whose inverse @OneToMany mappings deliberately reference context entities (DDD
    // Shared Kernel). It is excluded from this rule so the downward kernel->context coupling is
    // legitimised, not hidden. The remaining freeze still guards the residual orchestrator leak
    // (ProjectServiceImpl/ProjectResponse aggregating ai/collaboration); it only shrinks.
    ArchRule rule = freeze(classes()
        .that().resideInAPackage("..shared..")
        .and().resideOutsideOfPackage("..shared.kernel..")
        .should().onlyDependOnClassesThat()
        .resideOutsideOfPackages("..ai..", "..audit..", "..collaboration.."));

    rule.check(productionClasses);
  }

  @Test
  void serviceLayerShouldNotExposeResponseEntity() {
    ArchRule rule = freeze(classes()
        .that().resideInAPackage("..service..")
        .and().resideOutsideOfPackage("..controller..")
        .should().onlyDependOnClassesThat()
        .doNotHaveFullyQualifiedName("org.springframework.http.ResponseEntity"));

    rule.check(productionClasses);
  }

  @Test
  void repositoriesShouldBeNamedRepository() {
    // Rule 3 (plan §10.7): every Spring Data repository ends with 'Repository'. Strict since
    // Étape 17: the last '*Repo' was eradicated at Étape 14, the frozen set is now empty.
    ArchRule rule = classes()
        .that().areAssignableTo(org.springframework.data.repository.Repository.class)
        .and().areInterfaces()
        .should().haveSimpleNameEndingWith("Repository");

    rule.check(productionClasses);
  }
}
