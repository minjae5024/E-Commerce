package minjae5024.ECommerceProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    long id;
    String name;
    long purchase_count;
}
