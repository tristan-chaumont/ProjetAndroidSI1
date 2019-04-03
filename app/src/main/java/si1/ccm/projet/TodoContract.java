package si1.ccm.projet;

import android.provider.BaseColumns;

/**
 * Created by phil on 11/02/17.
 */

public final class TodoContract {
    public static class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_TAG = "tag";
        public static final String COLUMN_NAME_DONE = "done";
        public static final String COLUMN_NAME_ECHEANCE = "echeance";
    }
}
