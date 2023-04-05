package com.poc1.orders.repositories;

import com.poc1.orders.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Integer>{

    @Modifying
    @Transactional
    @Query(value = "delete from orders where order_id = ?1",nativeQuery = true)
    int retainDataBeforeOrderIdDelete(Integer orderId);

    @Modifying
    @Transactional
    @Query(value = "delete from orders where user_id = ?1",nativeQuery = true)
    int retainDataBeforeUserIdDelete(String userId);

   public Optional<List<Orders>> findByUserId(String userId);
}
