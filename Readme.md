## About The Project

SixthSense @**hacktothefuture2022**

Sixth Sense is a solution to digitize car stores. 
Car stores are still quite analogue. Devising ways to access the entire catalog and make the whole experience more digital.


### Built With

The following framework and libraries were used while developing the sixth sense prototype application.
* [Android](https://developer.android.com/)
* [Kotlin](https://kotlinlang.org/)
* [AR Sceneform](https://developers.google.com/sceneform/develop)
* [AR core SDK](https://developers.google.com/ar)
* [Gradle](https://gradle.org/)
* [Github Actions](https://github.com/features/actions)


## Getting Started

### Adding Dependencies

Add the following dependencies to your app level build.gradle file:

```sh
implementation 'com.google.ar.sceneform.ux:sceneform-ux:1.9.0'
```

### Updating Manifest
Add the following lines in your AndroidManifest.xml file:

```sh
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET"/>
<uses-feature android:name="android.hardware.camera.ar" android:required="true"/>
```

Using ARCore requires camera permission and a camera enabled phone (obviously).

Also, add a meta data to your application tag:

```sh
<meta-data
   android:name="com.google.ar.core"
   android:value="required" />
```

If your app strictly requires the device to be ARCore enabled, then set required = true or if AR is not a primary feature or you have handled compatibility for non-compatible devices you can set required = false.

### Adding the ArFragment
With all the initial set up done, it is now time to start by adding a ArFragment (provided in the Sceneform SDK) into our app. ArFragment automatically handles your sessions and the runtime checks necessary for the application to work.

If ARCore has not been installed on the user’s device, ArFragment urges the user to install ARCore. Also, if camera permission is not granted, it asks for camera permission as well. Hence, ArFragment is the best way to start building your very first Android ARCore application.

But if your app still needs some extended functionality, you can always subclass ArFragment and create a Fragment of your own to support your custom features.

Here’s how your layout xml file would look like:

```sh
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
   xmlns:tools="http://schemas.android.com/tools"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   tools:context=".HelloSceneformActivity">

 <fragment android:name="com.google.ar.sceneform.ux.ArFragment"
     android:id="@+id/ux_fragment"
     android:layout_width="match_parent"
     android:layout_height="match_parent" />

</FrameLayout>
```
### Checking compatibility at runtime
We will check if the device:

Is running Android API version >= 24.
Can support OpenGL version 3.0.
The above conditions are mandatory for a device to support AR applications using ARCore and Sceneform SDK.

We intend to finish the activity if these conditions aren’t satisfied. However, you can still continue to support other features.

Adding 3D models to our application
It is now time to download and import the 3D models to be rendered into our application. In our case, we will be rendering a 3D Car in a corner of our room and moving it around.

## Scope 

### Prototype (MVP)

- [x]     Render the Car AR model
- [x]     Controlls to move the car (Right,Left, Top Rotate, Bottom Rotate)
- [x]     Touch Control
- [x]     360 degreee view of the Car
  
[<img src="https://img.youtube.com/vi/zUnsM0gYuTQ/maxresdefault.jpg" width="50%">](https://youtu.be/zUnsM0gYuTQ)

### Future Roadmap

- [ ]     Interior Guided Tour
- [ ]     Engine Guided Tour
- [ ]     Exterior Guided Tour (show dents or scratches)    


## Acknowledgments

* [AR Sceneform](https://developers.google.com/sceneform/develop)
* [AR core SDK](https://developers.google.com/ar)
* [AR Samples](https://developers.google.com/ar/develop/samples)


