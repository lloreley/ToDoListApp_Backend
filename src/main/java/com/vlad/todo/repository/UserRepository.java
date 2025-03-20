package com.vlad.todo.repository;

import com.vlad.todo.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findById(long id);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    void deleteById(long id);
}
