package services.word.impl;

import models.word.ArticleLink;
import repository.word.ArticleLinkRepository;
import services.base.ModelService;

import javax.inject.Inject;

public class ArticleLinkServiceImpl extends ModelService<ArticleLink> {

    protected final ArticleLinkRepository repository;

    @Inject
    public ArticleLinkServiceImpl(ArticleLinkRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
