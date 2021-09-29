package com.example.moviecatalogservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient.Builder builder;

   @Autowired
   private DiscoveryClient discoveryClient;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable(name ="userId") String userId){

       UserRating userRating = restTemplate.getForObject("http://rating-service/ratings/users/"+userId, UserRating.class);


        return userRating.getUserRatings().stream().map(
                rating-> {
                   Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);

                /*    Movie movie=  builder.build()
                            .get()
                            .uri("http://localhost:8082/movies/"+rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)
                            .block();*/
                   return  new CatalogItem(movie.getName(), "1.0",rating.getRating());
                }
        ).collect(Collectors.toList());
    }
}
