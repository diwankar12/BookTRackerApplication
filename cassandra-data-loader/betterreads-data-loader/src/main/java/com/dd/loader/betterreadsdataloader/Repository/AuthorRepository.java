package com.dd.loader.betterreadsdataloader.Repository;

import com.dd.loader.betterreadsdataloader.Entity.Author;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CassandraRepository<Author,String> {
}
