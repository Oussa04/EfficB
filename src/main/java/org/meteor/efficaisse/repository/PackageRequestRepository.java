package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.PackageRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface PackageRequestRepository  extends CrudRepository<PackageRequest,Integer> {
    List<PackageRequest> findAllByDateBefore(Date date);

    int countAllByDateAfter(Date date);

}
