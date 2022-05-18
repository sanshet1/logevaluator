package com.cs.logevaluator.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cs.logevaluator.entity.EventAlert;

@Repository
public interface EventAlertRepository  extends CrudRepository<EventAlert, String> {

}
