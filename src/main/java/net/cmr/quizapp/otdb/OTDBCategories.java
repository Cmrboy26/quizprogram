package net.cmr.quizapp.otdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OTDBCategories {

    @JsonProperty("trivia_categories")
    private List<OTDBCategory> triviaCategories;

    public List<OTDBCategory> getTriviaCategories() {
        return triviaCategories;
    }

    public void setTriviaCategories(List<OTDBCategory> triviaCategories) {
        this.triviaCategories = triviaCategories;
    }

    public static class OTDBCategory {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OTDBCategories{\n");
        sb.append("  triviaCategories=");
        if (triviaCategories != null) {
            sb.append("[\n");
            for (int i = 0; i < triviaCategories.size(); i++) {
                OTDBCategory cat = triviaCategories.get(i);
                sb.append("    {id=").append(cat.getId())
                  .append(", name='").append(cat.getName()).append("'}");
                if (i < triviaCategories.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("  ]");
        } else {
            sb.append("null");
        }
        sb.append("\n}");
        return sb.toString();
    }
}
