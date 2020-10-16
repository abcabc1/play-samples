package repository.word;

import io.ebean.ExpressionList;
import models.word.WordEn;
import repository.base.ModelRepository;

public class WordEnRepository extends ModelRepository<WordEn> {

    public WordEnRepository() {
        super("word");
    }

    @Override
    public Object getPk(WordEn model) {
        return model.id;
    }

    public WordEn getById(Object id) {
        return WordEn.find.byId((Long)id);
    }

    /* 
    remove it if sort by id   
    */
    @Override
    public String getSort() {
        return "id desc";
    }

    @Override
    public ExpressionList<WordEn> getExpr(WordEn model) {
        ExpressionList<WordEn> expressionList = getExpressionList(model);
        /*
        if (model.id != null) {
            expressionList.eq("id", model.id);
        }
        */
        return expressionList;
    }
}