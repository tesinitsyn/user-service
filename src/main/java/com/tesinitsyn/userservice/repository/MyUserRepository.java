package com.tesinitsyn.userservice.repository;

import com.tesinitsyn.userservice.models.MyUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserRepository extends CrudRepository<MyUser, Long> {
    Optional<MyUser> findByEmail(String email);
}
