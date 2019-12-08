package com.ing.graphql.marvel.resolvers;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.ing.graphql.marvel.domain.Character;
import com.ing.graphql.marvel.service.MarvelService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class RootQuery implements GraphQLQueryResolver {

    private MarvelService marvelService;

    public RootQuery(MarvelService marvelService) {
        this.marvelService = marvelService;
    }

    public List<Character> getCharacters(final int limit) throws IOException {
        return this.marvelService.getAllCharacters(limit);
    }

    public Optional<Character> getCharacter(final int id) throws IOException {
        return this.marvelService.getCharacter(id);
    }

}

