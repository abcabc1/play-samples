package services.word.impl;

import models.word.WordEnArticle;
import repository.word.WordEnArticleRepository;
import services.base.BaseService;

import javax.inject.Inject;

public class WordEnArticleService extends BaseService<WordEnArticle> {

    @Inject
    public WordEnArticleService(WordEnArticleRepository repository) {
        super(repository);
    }
}
