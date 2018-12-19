package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Licence;
import org.meteor.efficaisse.model.Store;
import org.meteor.efficaisse.model.StoreType;
import org.meteor.efficaisse.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface StoreRepository extends CrudRepository<Store, String> {
    Store findByManager(User u);

    int countAllByPayDateAfterAndPackEquals(Date date, String pack);
    int countAllByPayDateLessThanAndPackEqualsAndManagerEnabled(Date date, String name, Boolean enabled);
    int countAllByPayDateLessThanAndLicence_IdEqualsAndManagerEnabled(Date date, int licence, Boolean enabled);
    List<Store> findAllByLicenceIs(Licence l);

    Store findByRegisterDC(String rdc);

    @Query(value = "SELECT DISTINCT  s.pack AS pack, COUNT (s) as nbr  FROM Store s GROUP BY  s.pack order by nbr DESC ")
    List<Object> showNbrLicence (Pageable page);

    @Query(value = "SELECT DISTINCT s.type.name AS storeType, COUNT (s) as nbr  FROM  Store s  GROUP BY  s.type ")
    List<Object> showNbrOfStoresByType (Pageable page);


}
