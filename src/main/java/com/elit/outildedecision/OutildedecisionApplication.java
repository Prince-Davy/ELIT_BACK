package com.elit.outildedecision;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.util.ProjectConfirmationState;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Objects;

@SpringBootApplication
@EnableSwagger2
public class OutildedecisionApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutildedecisionApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    CommandLineRunner start(UserRepository userRepository,
                            ProjectRepository projectRepository,
                            MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
                            PredictibilityAnalysisRepository predictibilityAnalysisRepository,
                            ProjectUserPermissionRepository projectUserPermissionRepository,
                            ExecutionPlanQuestionRepository executionPlanQuestionRepository,
                            VoteRepository voteRepository,
                            MethodologyRepository methodologyRepository,
                            MethodRepository methodRepository,
                            SurveyQuestionRepository surveyQuestionRepository
    ) {
        return args -> {
            //TODO For future versions: it's better to have the data in json files

            if (Objects.isNull(userRepository.findByUsername("user"))) {
                User chef = userRepository.save(User.builder()
                        .username("user")
                        .userEmail("user@user.com")
                        .userLastName("Doe")
                        .userFirstName("John")
                        .userJob("Développeur front")
                        .isUserActive(true)
                        .isEmailVerified(true)
                        .userPassword(passwordEncoder().encode("user"))
                        .isAdmin(false)
                        .build());
            }

            if (Objects.isNull(userRepository.findByUsername("admin"))) {
                User admin = userRepository.save(User.builder()
                        .userEmail("admin@elite.fr")
                        .userPassword(passwordEncoder().encode("admin"))
                        .userFirstName("Nancy")
                        .username("admin")
                        .userLastName("Michel")
                        .userJob("Chef de projet")
                        .isEmailVerified(true)
                        .isUserActive(true)
                        .isAdmin(true)
                        .build());
            }
            if (Objects.isNull(userRepository.findByUsername("contributer"))) {
                User contributer = userRepository.save(User.builder()
                        .userEmail("contributer@elit.com")
                        .userPassword(passwordEncoder().encode("contributer"))
                        .userFirstName("Charles")
                        .username("contributer")
                        .userLastName("Baker")
                        .userJob("Développeur back")
                        .isEmailVerified(true)
                        .isUserActive(true)
                        .isAdmin(false)
                        .build());
            }
            if (Objects.isNull(userRepository.findByUsername("observer"))) {
                User observer = userRepository.save(User.builder()
                        .userEmail("observer@elit.com")
                        .userPassword(passwordEncoder().encode("observer"))
                        .userFirstName("Rochelle")
                        .username("observer")
                        .userLastName("Wright")
                        .userJob("Développeur fullstack")
                        .isEmailVerified(true)
                        .isUserActive(true)
                        .isAdmin(false)
                        .build());
            }

            //Execution plan initialization
            if (executionPlanQuestionRepository.count()==0) {
                executionPlanQuestionRepository.save(ExecutionPlanQuestion.builder()
                        .question("Ce que l’entreprise privilégie au niveau méthodologies")
                        .title("Stratégie d’organisation")
                        .build());

                executionPlanQuestionRepository.save(ExecutionPlanQuestion.builder()
                        .question("Spécificités du projet, contraintes techniques")
                        .title("Environnement de travail")
                        .build());

                executionPlanQuestionRepository.save(ExecutionPlanQuestion.builder()
                        .question("D’éventuels outils ou pratiques spécifiques à ce secteur")
                        .title("Secteur d’activité économique")
                        .build());

                executionPlanQuestionRepository.save(ExecutionPlanQuestion.builder()
                        .question("Le domaine peut avoir des aspects ou spécificités bénéficiant de l’apport de l’informatique.")
                        .title("Domaine technique d’application")
                        .build());
            }

            //Methodology-Methods
            //Classique
            String name = "Classique";
            if (Objects.isNull(methodologyRepository.findMethodologyByName(name))) {
                Methodology classique = methodologyRepository.save(Methodology.builder()
                        .name(name)
                        .description("Avant l’avènement des méthodes agiles, des solutions dites « classiques/traditionnelle » sont considérées comme les précurseurs et sont toujours utilisées aujourd’hui. Les plus connus sont cycle en V,cascade, spirale, cycle en Y.")
                        .advantages("Il y a assez de bibliographie sur ces méthodes, avec plusieurs cas réel d'application, pouvant servir de base pour savoir quand appliquer, quand éviter, comment résoudre un problèm rencontré, etc. La maitrise du budget et du délai s'avère plus facile. ")
                        .disadvantages("Ils ne sont pas centrés à l'ouverture aux changements, même si  quelques uns (e.g. spirale) peuvent prendre les changements sans que le projet soit en danger. Pour quelques uns entre eux, l'effet tunnel peut prendre des dimensions importantes, si le besoin du client change après la description initiale du besoin.")
                        .build());
                methodRepository.save(Method.builder()
                        .name("Cascade")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(classique)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Cycle en V")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(classique)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Spirale")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(classique)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Cycle en Y")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(classique)
                        .build());
            }
            //Agile
            name = "Agile";
            if (Objects.isNull(methodologyRepository.findMethodologyByName(name))) {
                Methodology agile = methodologyRepository.save(Methodology.builder()
                        .name(name)
                        .description("Une approche itérative et incrémentale du développement de logiciels, qui est réalisée de manière collaborative par des équipes auto-organisées dans un cadre de gouvernance efficace. La cérémonie est «juste assez» pour produire des logiciels de haute qualité de manière rentable et rapide et qui répondent aux l'évolution des besoins de ses parties prenantes.")
                        .advantages("L’apport de valeur constant : en effectuant des livraisons fonctionnelles et opérationnelles régulières, les équipes agiles mettent le fruit de leur travail à disposition des utilisateurs et clients en fonction de la valeur apportée. Un Time to market optimisé : l’accent étant mis sur la réalisation des fonctionnalités apportant le plus de valeur au client, un MVP peut être mis à disposition plus rapidement.")
                        .disadvantages("L’ambigüité de la vision produit : Les développements étant réalisés sur des fonctionnalités, un projet peut manquer de vision globale. La réalisation de fonctionnalités, réalisées séparément les unes des autres, peut apporter de la confusion dans la réalisation d’un projet global. La gestion à long terme : La volatilité des priorités ajoute un risque sur l’homogénéisation du produit. Le développement sans priorisation, ni vision globale apporte un risque d’éparpillement non négligeable.")
                        .build());
                methodRepository.save(Method.builder()
                        .name("Kanban")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Scrum")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
                methodRepository.save(Method.builder()
                        .name("XP")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Scrumban")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Lean")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Dad")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Safe")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Less")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Nexux")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
                methodRepository.save(Method.builder()
                        .name("Scrum of Scrums")
                        .description("description")
                        .advantages("advantages")
                        .disadvantages("disadvantages")
                        .methodology(agile)
                        .build());
            }
            //Hybride
            name = "Hybride";
            if (Objects.isNull(methodologyRepository.findMethodologyByName(name))) {
                Methodology hybride = methodologyRepository.save(Methodology.builder()
                        .name(name)
                        .description("Une association d’approches prédictives, itératives, incrémentales ou agiles. Cette méthode hybride ferait coexister une méthodologie de projet Agile, avec une méthodologie de projet prédictive, dans le cas où certaines parties du cycle de développement seraient confiées à des entités tierces.")
                        .advantages("La coexistence des deux précédents méthodes a pour but d'atténuer leurs inconvénients tout en gardant leurs avantages. Alors, lister les avantages et inconvénients n'est plus le cas. Il faut par contre faire attention aux points :  \n" +
                                "- bien mesurer le périmètre de ce qui sera prédictif ou agile, en s’assurant de la bonne couverture des exigences fonctionnelles et/ou techniques dans le délai et le budget consenti.\n" +
                                "- l’articulation des méthodes ou la dominante méthodologique dépendra des usages des équipes, de leur acculturation à l’agilité, ou du périmètre technique ou fonctionnel impacté. \n" +
                                "- dans le cas d ‘une sous-traitance d’un ou plusieurs lots auprès d’une ESN, il conviendra de bien apprécier l’incertitude liée aux exigences techniques, le taux de changement ou le risque minimum d'exécution. \n" +
                                "- la disponibilité et la qualité des composants sous-traités sera également un risque à couvrir pour ne pas mettre en péril les itérations à venir et/ou le déploiement des fonctionnalités. ")
                        .disadvantages("")
                        .build());
            }



            if(Objects.isNull(surveyQuestionRepository.findByQuestion("Quelle est votre satisfaction globale concernant votre expérience avec ELIT ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("Quelle est votre satisfaction globale concernant votre expérience avec ELIT ?")
                        .type("satisfaction")
                        .build());
            }

            if(Objects.isNull(surveyQuestionRepository.findByQuestion("A quel point diriez-vous que ELIT est facile à utiliser ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("A quel point diriez-vous que ELIT est facile à utiliser ?")
                        .type("difficuté")
                        .build());
            }

            if(Objects.isNull(surveyQuestionRepository.findByQuestion("Diriez-vous que le processus de décision vous a aidé à identifier les limitations du projet ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("Diriez-vous que le processus de décision vous a aidé à identifier les limitations du projet ?")
                        .type("accord")
                        .build());
            }

            if(Objects.isNull(surveyQuestionRepository.findByQuestion("Diriez-vous que le processus de décision vous a aidé à résoudre les difficultés identifiées ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("Diriez-vous que le processus de décision vous a aidé à résoudre les difficultés identifiées ?")
                        .type("accord")
                        .build());
            }

            if(Objects.isNull(surveyQuestionRepository.findByQuestion("Diriez-vous que l’évaluation des paramètres et des critères ainsi que leur terminologie était claire ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("Diriez-vous que l’évaluation des paramètres et des critères ainsi que leur terminologie était claire ?")
                        .type("accord")
                        .build());
            }

            if(Objects.isNull(surveyQuestionRepository.findByQuestion("Diriez-vous que les rapports générés via le processus sont utiles pour le projet ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("Diriez-vous que les rapports générés via le processus sont utiles pour le projet ?")
                        .type("accord")
                        .build());
            }

            if(Objects.isNull(surveyQuestionRepository.findByQuestion("Diriez-vous que la présence d’un facilitateur pendant l’exécution du processus a été utile ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("Diriez-vous que la présence d’un facilitateur pendant l’exécution du processus a été utile ?")
                        .type("accord")
                        .build());
            }

            if(Objects.isNull(surveyQuestionRepository.findByQuestion("Diriez-vous que les outils utilisés pendant l’exécution du processus de décision ont été utiles ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("Diriez-vous que les outils utilisés pendant l’exécution du processus de décision ont été utiles ?")
                        .type("accord")
                        .build());
            }
            if(Objects.isNull(surveyQuestionRepository.findByQuestion("Selon-vous, qu’est-ce qui pourrait améliorer votre expérience avec l’application ELIT ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("Selon-vous, qu’est-ce qui pourrait améliorer votre expérience avec l’application ELIT ?")
                        .type("amélioration")
                        .build());
            }
            if(Objects.isNull(surveyQuestionRepository.findByQuestion("Sur une échelle de 0 à 10, quelle est la probabilité que vous recommandiez ELIT à un collègue ?"))){
                surveyQuestionRepository.save(SurveyQuestion.builder()
                        .question("Sur une échelle de 0 à 10, quelle est la probabilité que vous recommandiez ELIT à un collègue ?")
                        .type("amélioration")
                        .build());
            }

           /* for (int i=0; i<100 ; i++){
                projectRepository.save(Project.builder()
                                .name("test"+i)
                                .chefId(1L)
                                .isArchived(false)
                                .confirmationState(ProjectConfirmationState.WAITING.name())
                        .build());            }*/

        };
    }
}
