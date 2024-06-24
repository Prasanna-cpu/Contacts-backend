package net.kumar.backend.Repository;

import net.kumar.backend.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ContactRepository extends JpaRepository<Contact,String> {

    Optional<Contact> findByEmail(String email);
}
