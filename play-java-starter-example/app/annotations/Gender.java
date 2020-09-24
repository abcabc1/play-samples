package annotations;

public enum Gender {

    MALE("男", 1),
    FEMALE("女", 2);

    private String genderCn;
    private Integer index;

    Gender(String genderCn, Integer index) {
        this.genderCn = genderCn;
        this.index = index;
    }

    public static Gender getByName(String genderCn) {
        if (genderCn == null) return null;
        for (Gender gender : Gender.values()) {
            if (gender.genderCn.equals(genderCn)) {
                return gender;
            }
        }
        return null;
    }

    public static Gender getByIndex(Integer index) {
        if (index == null) return null;
        for (Gender gender: Gender.values()) {
            if (gender.index.equals(index)) {
                return gender;
            }
        }
        return null;
    }

    public String getGenderCn() {
        return genderCn;
    }

    public void setGenderCn(String genderCn) {
        this.genderCn = genderCn;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
