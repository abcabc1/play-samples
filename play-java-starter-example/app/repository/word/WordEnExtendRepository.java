package repository.word;

import io.ebean.ExpressionList;
import models.word.WordEnExtend;
import models.word.WordEnExtendPk;
import repository.base.ModelRepository;

public class WordEnExtendRepository extends ModelRepository<WordEnExtend> {

    public WordEnExtendRepository() {
        super("word");
    }

    @Override
    public Object getPk(WordEnExtend model) {
        return model.pk;
    }

    public WordEnExtend getById(Object id) {
        return WordEnExtend.find.byId((WordEnExtendPk) id);
    }
    /* 
    remove it if sort by id   
    */
    @Override
    public String getSort() {
        return "id desc";
    }

    @Override
    public ExpressionList<WordEnExtend> getExpr(WordEnExtend model) {
        ExpressionList<WordEnExtend> expressionList = getExpressionList(model);
        /*
        if (model.id != null) {
            expressionList.eq("id", model.id);
        }
        */
        return expressionList;
    }
}