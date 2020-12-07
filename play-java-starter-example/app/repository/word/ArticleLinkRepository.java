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
        if (model.articlePage != null) {
            expressionList.eq("articlePage", model.articlePage);
        }
        if (model.articlePageFrom != null) {
            expressionList.ge("articlePage", model.articlePageFrom);
        }
        if (model.articlePageTo != null) {
            expressionList.le("articlePage", model.articlePageTo);
        }
        if (model.articleIndexFrom != null) {
            expressionList.ge("articleIndex", model.articleIndexFrom);
        }
        if (model.articleIndexTo != null) {
            expressionList.le("articleIndex", model.articleIndexTo);
        }
        if (model.source != null) {
            expressionList.eq("source.node", model.source.node);
        }
        return expressionList;
    }
}