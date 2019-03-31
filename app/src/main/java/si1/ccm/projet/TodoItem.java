package si1.ccm.projet;

import java.util.Date;

/**
 * Created by phil on 06/02/17.
 */

public class TodoItem {

    public enum Tags {
        Faible("Faible"), Normal("Normal"), Important("Important");

        private String desc;
        Tags(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    private long id;
    private String label;
    private Tags tag;
    private boolean done;

    public TodoItem(Tags tag, String label) {
        this.tag = tag;
        this.label = label;
        this.done = false;
        this.id = 0;
    }

    public TodoItem(String label, Tags tag, boolean done) {
        this.label = label;
        this.tag = tag;
        this.done = done;
    }

    public TodoItem(long id, String label, Tags tag, boolean done) {
        this.id = id;
        this.label = label;
        this.tag = tag;
        this.done = done;
    }

    public static Tags getTagFor(String desc) {
        for (Tags tag : Tags.values()) {
            if (desc.compareTo(tag.getDesc()) == 0)
                return tag;
        }

        return Tags.Faible;
    }

    public String getLabel() {
        return label;
    }

    public Tags getTag() {
        return tag;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setTag(Tags tag) {
        this.tag = tag;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }
}
