package org.udesa.giftcards.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.udesa.giftcards.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}

