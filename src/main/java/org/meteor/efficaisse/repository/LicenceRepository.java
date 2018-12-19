package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Licence;
import org.springframework.data.repository.CrudRepository;

public interface LicenceRepository  extends CrudRepository<Licence,Integer> {
   Licence findByName(String name);
   Licence findFirstByNameOrLength(String name, int length);
   Licence findFirstByLength(int length);
}
