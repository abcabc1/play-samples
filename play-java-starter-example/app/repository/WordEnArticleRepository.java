package repository;

import io.ebean.ExpressionList;
import models.word.WordEnArticle;
import play.db.ebean.EbeanConfig;
import repository.base.BaseRepository;

import javax.inject.Inject;

public class WordEnArticleRepository extends BaseRepository<WordEnArticle> {

    @Inject
    public WordEnArticleRepository(EbeanConfig ebeanConfig) {
        super(ebeanConfig, "word");
    }

    @Override
    public Object getPk(WordEnArticle model) {
        return model.id;
    }

    /* 
    remove it if sort by id   
    */
    @Override
    public String getSort() {
        return "id desc";
    }

    @Override
    public ExpressionList<WordEnArticle> getExpr(WordEnArticle model) {
        ExpressionList<WordEnArticle> expressionList = getExpressionList(model);
        if (model.id != null) {
            expressionList.eq("id", model.id);
        }
        return expressionList;
    }
}