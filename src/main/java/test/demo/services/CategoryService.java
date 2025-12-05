package test.demo.services;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import test.demo.entity.Category;

public interface CategoryService {
    void delete(Category entity);

    // Sửa Long thành Integer
    void deleteById(Integer id);

    long count();

    <S extends Category> Optional<S> findOne(Example<S> example);

    // Sửa Long thành Integer
    Optional<Category> findById(Integer id);

    // Sửa Long thành Integer
    List<Category> findAllById(Iterable<Integer> ids);

    List<Category> findAll(Sort sort);

    Page<Category> findAll(Pageable pageable);

    List<Category> findAll();

    Optional<Category> findByCategoryName(String name);

    <S extends Category> S save(S entity);

    Page<Category> findByCategoryNameContaining(String name, Pageable pageable);

    List<Category> findByCategoryNameContaining(String name);
}