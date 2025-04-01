INSERT INTO project_user_permission (user_id, project_id, permission)
SELECT 1, project_id, 'CDP'
FROM project;
