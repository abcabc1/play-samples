package repository.word;

import io.ebean.ExpressionList;
import models.word.WordEnSentence;
import models.word.WordEnSentencePk;
import repository.base.BaseRepository;

public class WordEnSentenceRepository extends BaseRepository<WordEnSentence> {

    public WordEnSentenceRepository() {
        super("word");
    }

    @Override
    public Object getPk(WordEnSentence model) {
        return model.pk;
    }

    public WordEnSentence getById(Object id) {
        return WordEnSentence.find.byId((WordEnSentencePk)id);
    }

    /* 
    remove it if sort by id   
    */
    @Override
    public String getSort() {
        return "id desc";
    }

    @Override
    public ExpressionList<WordEnSentence> getExpr(WordEnSentence model) {
        ExpressionList<WordEnSentence> expressionList = getExpressionList(model);
        /*
        if (model.id != null) {
            expressionList.eq("id", model.id);
        }
        */
        return expressionList;
    }
}