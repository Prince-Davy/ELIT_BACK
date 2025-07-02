-- Insert sample audit data following SOLID principles and database standards
INSERT INTO
  `audit` (
    `methodology`,
    `description`,
    `start_date`,
    `end_date`,
    `team_size`,
    `sprint_duration`,
    `release_cycle`,
    `risk_level`,
    `stakeholders`,
    `project_state`,
    `has_documentation`,
    `project_complexity`,
    `budget_constraints`,
    `created_at`,
    `project_id`,
    `user_id`
  )
VALUES
  (
    'AGILE',
    'Audit complet du projet de développement d''application web. Évaluation de la méthodologie Agile mise en place, analyse des processus de développement, et recommandations pour l''amélioration continue. Le projet présente une bonne structure organisationnelle avec des sprints bien définis.',
    '2024-01-15',
    '2024-06-30',
    8,
    'Sprint de 2 semaines avec planning, daily standups, review et retrospective',
    'Release mensuelle avec déploiement continu',
    'MEDIUM',
    'Product Owner, Scrum Master, Équipe de développement, Stakeholders métier, Équipe QA',
    'IN_PROGRESS',
    true,
    'MEDIUM',
    'Budget alloué de 150k€ avec contraintes de délai strictes pour le go-live',
    NOW (),
    1,
    1
  ),
  (
    'WATERFALL',
    'Audit méthodologique d''un projet traditionnel en cascade. Analyse de la phase de conception détaillée, évaluation des livrables et de la documentation projet. Identification des risques liés à la rigidité du processus et recommandations d''amélioration.',
    '2024-02-01',
    '2024-12-31',
    12,
    'Phases séquentielles de 6 semaines',
    'Release unique en fin de projet',
    'HIGH',
    'Chef de projet, Architecte technique, Équipe développement, Client final, Comité de pilotage',
    'PLANNING',
    true,
    'HIGH',
    'Budget fixe de 300k€ avec pénalités de retard contractuelles',
    NOW (),
    2,
    2
  ),
  (
    'SCRUM',
    'Évaluation d''implémentation Scrum sur projet mobile. Analyse de la vélocité de l''équipe, efficacité des cérémonies Scrum, et qualité des user stories. Recommandations pour optimiser les processus et améliorer la collaboration équipe.',
    '2024-03-10',
    '2024-09-15',
    6,
    'Sprints de 3 semaines avec focus sur la définition of done',
    'Release bi-mensuelle avec tests automatisés',
    'LOW',
    'Product Owner certifié, Scrum Master, Développeurs full-stack, UX Designer',
    'DEVELOPMENT',
    true,
    'LOW',
    'Budget flexible de 80k€ avec possibilité d''extension selon ROI',
    NOW (),
    3,
    1
  ),
  (
    'KANBAN',
    'Audit d''un processus Kanban pour maintenance applicative. Évaluation du flux de travail, analyse des goulots d''étranglement, et mesure du lead time. Recommandations pour l''optimisation du work in progress et l''amélioration continue.',
    '2024-01-08',
    '2024-04-30',
    4,
    'Flux continu avec limite WIP de 3 par colonne',
    'Déploiement continu à la demande',
    'LOW',
    'Tech Lead, Développeurs senior, Support technique, Responsable maintenance',
    'MAINTENANCE',
    false,
    'LOW',
    'Budget annuel de 50k€ pour maintenance évolutive et corrective',
    NOW (),
    4,
    3
  );

-- Add comments for documentation following SOLID principles
-- Single Responsibility: Each audit entry focuses on one specific methodology assessment
-- Open/Closed: Structure allows for extension with new audit types without modification
-- Liskov Substitution: All audit entries follow the same contract and can be used interchangeably
-- Interface Segregation: Clear separation of audit concerns (methodology, timeline, team, constraints)
-- Dependency Inversion: Audit depends on abstract project and user entities, not concrete implementations