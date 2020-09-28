package models.word;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Embeddable
@Entity
public class WordEnExtendPk {

    @Column(nullable = false, columnDefinition = "varchar(32) comment '单词'")
    public String word;
    @Column(nullable = false, columnDefinition = "varchar(16) comment '词类'")
    public String type;
    @Column(nullable = false, columnDefinition = "int comment '序号'")
    public int no;

    public int hashCode() {
        return word.hashCode() + type.hashCode() + no;
    }

    public boolean equals(Object o) {
        if (o instanceof WordEnExtendPk) {
            WordEnExtendPk pk = (WordEnExtendPk) o;
            return this.word.equals(pk.word) && this.type.equals(pk.type) && this.no == pk.no;
        }
        return false;
    }

    public String toString() {
        return this.word + " " + this.type + " " + this.no;
    }
}
