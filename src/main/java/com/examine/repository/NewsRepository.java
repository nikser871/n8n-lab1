package com.examine.repository;

import com.examine.entity.News;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface NewsRepository extends CrudRepository<News, String> {

}
