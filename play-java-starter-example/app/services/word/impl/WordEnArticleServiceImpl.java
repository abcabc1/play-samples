package services.word.impl;

import models.word.WordEnArticle;
import repository.word.WordEnArticleRepository;
import services.base.ModelService;

import javax.inject.Inject;

public class WordEnArticleServiceImpl extends ModelService<WordEnArticle> {

    @Inject
    public WordEnArticleServiceImpl(WordEnArticleRepository repository) {
        super(repository);
    }

}
