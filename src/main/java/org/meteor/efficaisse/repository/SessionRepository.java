package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Session;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SessionRepository extends CrudRepository<Session,Long> {

    List<Session> findAllByStore(Store store);
}
