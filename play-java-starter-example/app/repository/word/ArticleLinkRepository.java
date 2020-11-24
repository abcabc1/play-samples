package repository.word;

import io.ebean.ExpressionList;
import models.word.ArticleLink;
import models.word.WordEnArticle;
import repository.base.ModelRepository;

public class ArticleLinkRepository extends ModelRepository<ArticleLink> {

    public ArticleLinkRepository() {
        super("word");
    }

    @Override
    public Object getPk(ArticleLink model) {
        return model.id;
    }

    public ArticleLink getById(Object id) {
        return ArticleLink.find.byId((Long)id);
    }
    /* 
    remove it if sort by id   
    */
    @Override
    public String getSort() {
        return "id desc";
    }

    @Override
    public ExpressionList<ArticleLink> getExpr(ArticleLink model) {
        ExpressionList<ArticleLink> expressionList = getExpressionList(model);
        if (model.id != null) {
            expressionList.eq("id", model.id);
        }
        if (model.articleIndex != null) {
            expressionList.eq("articleIndex", model.articleIndex);
        }
        if (model.articleType != null) {
            expressionList.eq("articleType", model.articleType);
        }
        return expressionList;
    }
}