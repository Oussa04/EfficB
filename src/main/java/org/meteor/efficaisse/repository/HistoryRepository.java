package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.History;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoryRepository extends CrudRepository<History, Long> {
    List<History> findAllByStore(Store store);
}
