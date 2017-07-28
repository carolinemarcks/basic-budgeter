package com.cmarcksthespot.budget.api

import com.cmarcksthespot.budget.model.Status

class DefaultApiImpl extends DefaultApi {
  /**
    * ping
    *
    * ping server to check if it&#39;s up
    *
    */
  override def ping(): Status = Status("service is up")
}
