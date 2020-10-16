package services.word.impl;

import models.word.WordEnArticle;
import repository.word.WordEnArticleRepository;
import services.base.ModelService;

import javax.inject.Inject;

public class WordEnArticleService extends ModelService<WordEnArticle> {

    @Inject
    public WordEnArticleService(WordEnArticleRepository repository) {
        super(repository);
    }
}
