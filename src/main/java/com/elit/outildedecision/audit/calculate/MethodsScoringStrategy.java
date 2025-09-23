package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.util.MethodType;

public interface MethodsScoringStrategy {

  /**
   * @return the Method Type of the Audit (Scrum , Safe ...).
   */
  MethodType supportedType();

  /**
   * Calculate the Audit Score.
   * @param auditResponseModel : the Audit Response Model
   */
  void calculateScoring(AuditResponseModel auditResponseModel);

}
