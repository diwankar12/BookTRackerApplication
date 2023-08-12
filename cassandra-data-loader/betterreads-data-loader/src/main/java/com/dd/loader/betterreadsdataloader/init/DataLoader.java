package com.dd.loader.betterreadsdataloader.init;

import com.dd.loader.betterreadsdataloader.Entity.Author;
import com.dd.loader.betterreadsdataloader.Entity.Book;
import com.dd.loader.betterreadsdataloader.Repository.AuthorRepository;
import com.dd.loader.betterreadsdataloader.Repository.BookRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataLoader {


    @Autowired
    AuthorRepository authorRepository ;

    @Autowired
    BookRepository bookRepository ;

    @Value("${datadump.location.author}")
    private String authorDumpLocation ;

    @Value("${datadump.location.works}")
    private String worksDumpLocation ;

    private void initAuthor(){

        Path path = Paths.get(authorDumpLocation);
        try {
            Stream<String> lines = Files.lines(path);
            lines.forEach(line->{
                String json = line.substring(line.indexOf("{"));
                JSONObject jsonObject = new JSONObject(json);
                Author author = new Author();
                author.setName(jsonObject.optString("name"));
                author.setPersonalName(jsonObject.optString("name",null));
                author.setId(jsonObject.optString("key",null).replace("/authors/",""));
                authorRepository.save(author);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initWorks(){


        Path path = Paths.get(worksDumpLocation);
        //2009-12-11T01:57:19.964652
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try {
            Stream<String> lines = Files.lines(path);
            lines.forEach(line->{
                String json = line.substring(line.indexOf("{"));
                JSONObject jsonObject = new JSONObject(json);
                Book book = new Book();

                book.setId(jsonObject.getString("key").replace("/works/",""));
                book.setName(jsonObject.optString("title"));
                JSONObject description = jsonObject.optJSONObject("description");
                if(description!=null){
                    book.setDescription(description.optString("value"));
                }
                JSONObject createDateObj = jsonObject.optJSONObject("created");
                if(createDateObj!=null){
                    String dateStr = createDateObj.optString("value");
                    book.setPublishedDate(LocalDate.parse(dateStr,dateTimeFormatter) );
                }
                JSONArray coversIds = jsonObject.optJSONArray("covers") ;
                if(coversIds!=null){
                     List<String> ls = new ArrayList<>();
                      for(int i=0;i<coversIds.length();i++){
                           ls.add(coversIds.optString(i));
                      }
                      book.setCoverIds(ls);
                }

                JSONArray authorsJsonObj = jsonObject.optJSONArray("authors");
                if(authorsJsonObj!=null){
                    List<String> authorIdsList = new ArrayList<>();
                    for(int i=0;i<authorsJsonObj.length();i++){
                        JSONObject authorJsonObject = authorsJsonObj.getJSONObject(i);
                        String optString = authorJsonObject.optJSONObject("author").optString("key");
                        authorIdsList.add(optString.replace("/authors/","")) ;
                    }
                        book.setAuthorId(authorIdsList);
                    List<String> authorNames = authorIdsList.stream().map(id -> authorRepository.findById(id))
                            .map(optionalAuthor -> {
                                if (!optionalAuthor.isPresent()) return "Unknown Author";
                                else return optionalAuthor.get().getName();
                            }).collect(Collectors.toList());
                    book.setAuthorNames(authorNames);
                }
                bookRepository.save(book);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void start(){
        initAuthor();
        initWorks() ;

    }
}
