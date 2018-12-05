[ ![Download](https://api.bintray.com/packages/javid/maven/DroidPref/images/download.svg) ](https://bintray.com/javid/maven/DroidPref/_latestVersion)

# DroidPref
An abstract wrapper on SharedPreferences which provides versioning, migration, thread safety and caching.

### Add DroidPref to project
You need to make sure you have the JCenter repository included in the `build.gradle` file in the root of your project:

```
repositories {
    ...
    jcenter()
}
```

Next add a dependency in the `build.gradle` file of your app module.

```
implementation 'org.esmaeeli.droid:pref:1.x.x'
```
