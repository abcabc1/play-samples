package models.word;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Embeddable
@Entity
public class WordEnSentencePk {

    @Column(nullable = false, columnDefinition = "varchar(32) comment '英文单词'")
    public String word;
    @Column(nullable = false, columnDefinition = "varchar(16) comment '词类'")
    public String type;
    @Column(nullable = false, columnDefinition = "int comment '词类'")
    public int no;
    @Column(nullable = false, columnDefinition = "varchar(256) comment '例句'")
    public String sentence;

    public int hashCode() {
        return word.hashCode() + type.hashCode() + no + sentence.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof WordEnSentencePk) {
            WordEnSentencePk pk = (WordEnSentencePk) o;
            return this.word.equals(pk.word) && this.type.equals(pk.type) && (this.no == no) && this.sentence.equals(pk.sentence);
        }
        return false;
    }

    public String toString() {
        return this.word + " " + this.type + " " + this.no + " " + this.sentence;
    }
}
