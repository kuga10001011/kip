package org.kuga.kip.datastore;

import org.kuga.kip.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatastoreRepository extends JpaRepository<Datastore, Long> {

    List<Datastore> findDistinctByUsersContaining(User user);

}
