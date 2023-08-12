package com.dd.loader.betterreadsdataloader.Repository;

import com.dd.loader.betterreadsdataloader.Entity.Book;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CassandraRepository<Book,String> {
}
