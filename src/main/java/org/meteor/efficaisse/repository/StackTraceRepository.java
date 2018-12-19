package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.StackTrace;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface StackTraceRepository extends CrudRepository<StackTrace,Long> {

    int countAllByDateAfter(Date date);
}
