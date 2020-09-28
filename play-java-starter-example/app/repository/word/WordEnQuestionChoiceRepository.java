package repository.word;

import io.ebean.ExpressionList;
import models.word.WordEnQuestionChoice;
import repository.base.BaseRepository;

public class WordEnQuestionChoiceRepository extends BaseRepository<WordEnQuestionChoice> {

    public WordEnQuestionChoiceRepository() {
        super("default");
    }

    @Override
    public Object getPk(WordEnQuestionChoice model) {
        return model.id;
    }

    /* 
    remove it if sort by id   
    */
    @Override
    public String getSort() {
        return "id desc";
    }

    public WordEnQuestionChoice getById(Object id) {
        return WordEnQuestionChoice.find.byId((Long)id);
    }

    @Override
    public ExpressionList<WordEnQuestionChoice> getExpr(WordEnQuestionChoice model) {
        ExpressionList<WordEnQuestionChoice> expressionList = getExpressionList(model);
        /*
        if (model.id != null) {
            expressionList.eq("id", model.id);
        }
        */
        return expressionList;
    }
}