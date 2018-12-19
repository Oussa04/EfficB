package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Role;import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role,Long> {
   Role findById(String id);
}
