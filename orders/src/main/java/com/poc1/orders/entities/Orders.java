package com.poc1.orders.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    private String orderName;
    private Integer orderItems;
    private String userId;

}
