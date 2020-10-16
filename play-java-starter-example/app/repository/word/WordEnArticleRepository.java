package repository.word;

import io.ebean.ExpressionList;
import models.word.WordEnArticle;
import repository.base.ModelRepository;

public class WordEnArticleRepository extends ModelRepository<WordEnArticle> {

    public WordEnArticleRepository() {
        super("word");
    }

    @Override
    public Object getPk(WordEnArticle model) {
        return model.id;
    }

    public WordEnArticle getById(Object id) {
        return WordEnArticle.find.byId((Long)id);
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