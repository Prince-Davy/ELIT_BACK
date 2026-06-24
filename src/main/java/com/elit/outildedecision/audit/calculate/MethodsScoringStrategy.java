package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.shared.util.MethodType;

import java.util.Set;

/** Methods scoring strategy interface. */
public interface MethodsScoringStrategy {

  /**
   * @return the Method Type of the Audit (Scrum , Safe ...).
   */
  Set<MethodType> supportedTypes();

  /**
   * Calculate the Audit Score.
   * @param audit : the audit
   * @return audit score with different scores computed
   */
  AuditScore calculateScoring(Audit audit);

}
