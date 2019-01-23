package com.golfpvcc.teamscore.Database;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_FRONT_NINE;

public class MyRealmMigration implements RealmMigration {
    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
        // with the same object creation and query capabilities.
        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
        // renamed.
        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema realmSchema = realm.getSchema();

        if (oldVersion == 0) {
            RealmObjectSchema ScoreCardSchema = realmSchema.get("ScoreCardRecord");
            ScoreCardSchema
                    .addField("mMachineState", int.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.set("m_GameOptions", "test");
                            obj.setInt("mCurrentHole", 1);
                        }
                    });

            oldVersion++;
        }
        if (oldVersion == 1) {
            RealmObjectSchema ScoreCardSchema = realmSchema.get("ScoreCardRecord");
            ScoreCardSchema
                    .addField("mMachineState", int.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setInt("mMachineState", DISPLAY_FRONT_NINE);
                        }
                    });

            oldVersion++;
        }
    }
}
