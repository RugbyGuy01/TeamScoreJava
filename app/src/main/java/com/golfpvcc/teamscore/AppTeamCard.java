package com.golfpvcc.teamscore;

import android.app.Application;

import com.golfpvcc.teamscore.Database.MyRealmMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppTeamCard extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().schemaVersion(1).migration(new MyRealmMigration()).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
