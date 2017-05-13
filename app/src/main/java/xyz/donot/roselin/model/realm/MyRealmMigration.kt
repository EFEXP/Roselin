package xyz.donot.quetzal.model.realm


import io.realm.DynamicRealm
import io.realm.RealmMigration

class MyRealmMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
     //   val schema = realm.schema
     //   var oldVersionCode=oldVersion
     //   if(oldVersionCode==0L){
          //  oldVersionCode++
   //     }
    }
}
