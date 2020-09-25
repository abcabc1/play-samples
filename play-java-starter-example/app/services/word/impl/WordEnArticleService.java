package services.word.impl;

import models.word.WordEnArticle;
import repository.base.BaseRepository;
import services.base.BaseService;

public class WordEnArticleService extends BaseService<WordEnArticle> {

    public WordEnArticleService(BaseRepository<WordEnArticle> repository) {
        super(repository);
    }
}
