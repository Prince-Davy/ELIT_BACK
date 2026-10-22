package com.elit.outildedecision.audit.adapter;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditReportOwnershipAdapterTest {
  @Mock private AuditRepository repository;
  @InjectMocks private AuditReportOwnershipAdapter adapter;

  @Test
  void acceptsAuditBelongingToProject() {
    Project project = new Project();
    project.setProjectId(1L);
    Audit audit = new Audit();
    audit.setProject(project);
    when(repository.findById(10L)).thenReturn(Optional.of(audit));
    assertTrue(adapter.belongsToProject(10L, 1L));
  }

  @Test
  void rejectsAuditFromAnotherProject() {
    Project project = new Project();
    project.setProjectId(2L);
    Audit audit = new Audit();
    audit.setProject(project);
    when(repository.findById(10L)).thenReturn(Optional.of(audit));
    assertFalse(adapter.belongsToProject(10L, 1L));
  }

  @Test
  void rejectsMissingAudit() {
    when(repository.findById(10L)).thenReturn(Optional.empty());
    assertFalse(adapter.belongsToProject(10L, 1L));
  }

  @Test
  void rejectsAuditWithoutProject() {
    when(repository.findById(10L)).thenReturn(Optional.of(new Audit()));
    assertFalse(adapter.belongsToProject(10L, 1L));
  }
}
