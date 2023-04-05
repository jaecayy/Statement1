package com.poc1.users.repositories;

import com.poc1.users.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users,String> {

    @Modifying
    @Transactional
    @Query(value = "delete from users where ID = ?1",nativeQuery = true)
    int retainDataBeforeUserIdDelete(String usersId);
}
