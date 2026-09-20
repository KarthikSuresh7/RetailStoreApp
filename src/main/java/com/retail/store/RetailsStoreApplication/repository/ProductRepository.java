package com.retail.store.RetailsStoreApplication.repository;

import com.retail.store.RetailsStoreApplication.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
   Product findByProductId(long l);
}
