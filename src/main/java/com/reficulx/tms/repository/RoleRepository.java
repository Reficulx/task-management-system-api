package com.reficulx.tms.repository;

import com.reficulx.tms.models.ERole;
import com.reficulx.tms.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

  Optional<Role> findByName(ERole name);

}
