package test.demo.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.demo.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByProductNameContaining(String name);

	Page<Product> findByProductNameContaining(String name, Pageable pageable);

	Optional<Product> findByProductName(String name);

	Optional<Product> findByCreateDate(Date createAt);
}
