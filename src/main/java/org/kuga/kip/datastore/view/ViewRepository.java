package org.kuga.kip.datastore.view;

import org.kuga.kip.datastore.Datastore;
import org.kuga.kip.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ViewRepository extends JpaRepository<View, Long> {
    Optional<List<View>> findAllByDatastoreAndUsersContaining(Datastore datastore, User user);
}
