# Fluid Framework for Mobile (FluidM)

Fluid Framework for Mobile (FluidM) is a framework for writing native applications for iOS and Android. The business logic of an app is written in a single codebase. Most of the UI can be modeled too. This single codebase is highly readable, and unit testable in any environment or in the cloud. The code is native on Android, and translated to Objective-C so it becomes native on iOS too. On top of the business logic and UI model, FluidM provides native iOS code and native Android code, such that an application may not need to write additional code beyond the business logic / UI model. Nonetheless, the API remains exposed so that any amount of additional native code can be written. **FluidM provides**:

* A **data model**, so that the UI can be informed about changes to the data, and update accordingly and automatically
* A **view layout** model (optional), so that the screens can be specified in an OS agnostic way. The layout format is fluid, meaning that it will adapt to any screen size or device orientation. The framework provides a mechanism to use a different layout for different orientations (portrait vs landscape). The framework provides a mechanism to use a different layout for a specific OS. It seems that for most views, however, the layout for a screen can be the same. But there are cases where the user experience would suffer tremendously without customizing for the specific operating system. The layout model understands the FluidM data model, such that a reference from the view to the data can be coded conveniently.
* A **datastore** ecosystem for using sqlite. Databases and schemas are automatically created on a device. Upgrades are managed automatically. Queries, Inserts, and Updates are be checked at compile time, and your IDE can provide instant feedback. A query result list is parameterized with a class representing the table being query, which means your IDE can provide autocomplete for table properties. The API to interact with the datastore employes the [Builder](http://en.wikipedia.org/wiki/Builder_pattern) pattern, so queries, inserts, and updates are extremely readable. The commands are parameterized, so it is safe against sql injection.
* **Images** mechanism to choose the right image for the screen resolution
* **Logging** mechanism, with support for logging levels, which does the appropriate thing for the OS it is running on
* **Resource** service for loading resources from the file system in the OS appropriate way
* **Http** service for connecting to a URL via Get, Post, Put, and Delete.
* **UI** service to push and pop layouts, show alerts, show modal views
* **Push Notifications** service so the application can listen for push notifications

## Native Applications

Users expect apps running on their devices to behave in certain ways. They expect them to be fast, reliable, and similar to other apps running on that device. If an app doesn't behave in that way, users become frusterated and give bad ratings. Facebook went through this experience when they built an HTML5 app, and then rewrote it from the ground up. They have a great write up on [Rebuilding Facebook for iOS](https://www.facebook.com/notes/facebook-engineering/under-the-hood-rebuilding-facebook-for-ios/10151036091753920).

FluidM recognizes the importance of deliverying a good user experience, and why it is so important to build native apps for each each OS, rather than an app that feels like a web form. 

FluidM takes this into account, and either handles these differences automatically, or provides a way for the developer to handle the differences. Below are just a couple examples of differences between iOS and Android:

* In button in the upper left corner on iOS is a **back** button. This button traverses backwards through the entire view stack. On Android, however, this button is an **up** button. This button pops all the views off the stack for the current activity. On Android, to go back, the hard key is utilized.
* A tabbed navigation application on iOS places the tabs at the bottom of the screen. On Android, however, this would make it too easy for a user to accidently click a hard key. Therefore, tabs are located near the top of the screen on Android.

## Quick links

* [Tech Stack](#tech-stack)
  * [Auto Generate Scripts](#auto-generate-scripts)
* [Installation](#installation)
  * [Project Setup](#project-setup)
* [Developer Guide](#developer-guide)
  * [Data Model](#data-model)
    * [Registering data models](#registering-data-models)
    * [Notifying of Data Model Changes](#notifying-of-data-model-changes)
    * [Listening to Data Model Changes](#listening-to-data-model-changes)
    * [Retreiving data model values](#retreiving-data-model-values)
  * [View Layout](#view-layout)
  * [Handling User Events](#handling-user-events)
  * [Datastore](#datastore)
  * [Images](#images)
  * [File Resources](#file-resources)
  * [Logging](#logging)
  * [Http Service](#http-service)
  * [UI Service](#ui-service)
  * [Application Initialization](#application-initialization)
* [Misc](#misc)
  * [KVL Format](#kvl-format)
  
## Tech Stack

The tech stack and primary programming language was selected after examining FluidM's goals, which are:

* Native code on both iOS (Objective-C) and Android (Java)
* Ability to set breakpoints and run the application using the debugger
  * Xcode for iOS
  * Eclipse for Android
* Ability to profile the application while running on a device
* Compile time checking to catch coding mistakes early
* Ability to run the business logic on any platform
* Ability to run unit testing in the cloud
* Ability to develop quickly (writing less lines of code)
* Access to efficient data structures

Based on these goals, the following tech stack was selected, which satisfies all of the forementioned goals.

* Java
  * The framework is written in this
  * Your application's business logic is written in this
* J2ObjC
  * Translates Java to Objective-C
  * A 20% project by some smart folks at Google
* Lombok
  * Writes Java code for you 
  * Specified through annotations in the code
  * From class variables, generates Getters, Setters, Equals, Hashcode, ToString, and Constructors
  * Writes the Builder pattern for you
  * Optional ability to turn all exceptions into runtime exceptions

Caveats:

* Android doesn't support the full Java API. Neither does J2ObjC. Therefore, the framework code and an application's code must use a subset of the full Java API. It turns that there is a ton of the API supported, so this has not been a limitation thus far.
* Lombok itself uses some API not supported by J2ObjC, so De-Lombok is run to remove Lombok annotations and distribute plain Java code, before J2ObjC runs.

### Auto Generate Scripts

There are several python scripts to do things automatically for the developer, reducing the amount of manual work.

* When the application's Java code is modified, a script runs to automatically
  1. De-Lombok the code
  1. Translate the code to Objective-C, and copy the output to your iOS app
  1. Jar the updated codebase for Android, and copy the output to your Android app
* When resource files are modified
  1. Copy the modified file to your iOS app
  1. Copy the modified file to your Android app
* When sql files (initial or upgrades) are added or modified
  1. Java class files are created, so that constants to table names and column names are created. These constants can be used for autocomplete in your IDE, and for compile time checking
  1. A list of datastore versions is generated, which is used by FluidM's datastore manager to manage the initial creation of the database on a user's device, and manage version upgrades on a user's device
  1. The version lists are copied to your iOS and your Android app
* When an image is added
  1. An images list file is generated, which is used by FluidM's image manager to manage which version of the image will be used at run time based on the device's resolution
  1. The image list is copied to your iOS and your Android app
* When a view layout is added
  1. A view list file is generated, which is used by FluidM's view manager to build the view model for your application at run time
  1. The view list is copied to your iOS and your Android app

## Installation

This guide assumes you are using Eclipse as your IDE.

After performing the first 2 steps below, you can run the [Sample iOS App](#running-the-sample-app). Completing the rest of the steps will allow you to run the sample Android app, and then create your own apps. Getting Eclipse setup the first time can take time.

1. Clone the repo
1. Download [J2ObjC](https://github.com/google/j2objc/wiki/Getting-Started) and put into FluidFrameworkIOS/FluidFrameworkIOS/Externals/j2objc (currently using version 0.9.3)
  * The contents inside of j2objc should be as follows
  ```
  / include
  / lib
  / man
  cycle_finder
  j2objc
  j2objcc
  ```
1. Add Google Play Services to Your Eclipse Workspace
  1. Instructions [here](https://developer.android.com/google/play-services/setup.html) for Using Eclipse with ADT
  1. Once google play services has been imported into your Eclipse workspace, then right click on FluidFrameworkAndroid, select Properties, and select Android. Fix the google play services library to point to the google play service project you imported.
1. Install lombok into Eclipse
  1. Find lombok.jar, located in FluidFramework/lib/lombok.jar
  1. Double click to run it
  1. Install it into Eclipse
  1. Restart Eclipse
1. In Eclipse, go to Preferences, Run/Debug, String Substitution. Set the variables in the following table using your path to fluid framework.
1. In Eclipse, import the existing projects into the workspace
  1. android-support-v7-appcompat *
  1. FluidFramework
  1. FluidFrameworkAndroid *
  1. FluidFrameworkAndroidSample *
  1. FluidFrameworkSample

* These should be imported as Android projects

Eclipse variables to set:

Variable | Value
-------- | ------
FLUID_FRAMEWORK_IOS | ../FluidFrameworkIOS/FluidFrameworkIOS
FLUID_FRAMEWORK_ANDROID | ../FluidFrameworkAndroid
FLUID_FRAMEWORK_SAMPLE_IOS | ../FluidFrameworkIOSSample/FluidFrameworkIOSSample
FLUID_FRAMEWORK_SAMPLE_ANDROID | ../FluidFrameworkAndroidSample

#### Running the Sample App

You should now be able to run the sample application, for both iOS and Android.

For iOS

* Open the Xcode project FluidFrameworkIOSSample, click Run

For Android, assuming you have the [Android SDK](http://developer.android.com/sdk/index.html), and that the ADT plugin for Eclipse is installed:

* From within Eclipse, Right click on FluidFrameworkAndroidSample, select Run As -> Android Application

#### Tips for running the Android Emulator

* Use a faster emulator, such as Genymotion, or

* Make sure the console outputs HAX is working and emulator runs in fast girt mode. If not, the emulator will be unbearable slow. See the tips below to setup an Android virtual device from within Eclipse.
* If a box comes up about Auto Monitor Logcat, choose yes at the debug level
* When the emulator comes up locked, you need to unlock it to launch your app

#### To setup an Android Virtual Device from within Eclipse

* Within Eclipse, open the Android Virtual Device Manager
* Switch tab to Device Definitions
* Choose a device. I used Nexus 4.
* Double click. Change buttons from Software to Hardware.
* Clone the device.
* Click Create AVD, using the cloned device.
* For CPU, select Intel Atom (x86)
* Skin can use with dynamic hardware controls
* Select an emulated camera
* Enter a value for the SD card, like 16 GB
* Check the Use Host GPU box (or else your emulator will run really slow)

### Project Setup

The recommended approach for setting up a project to use Fluid Framework is to use a Git Submodule pointing to Fluid Framework. You should then setup 3 projects: one for your application code, one for your iOS app, and one for your Android app. Your folders should be structured as:

```
/ Your Root Folder for Projects using FluidM
/ fluid-framework
/ Your Project Root Folder
  / Your App
  / Your App iOS
  / Your App Android
/ Another Project Root Folder
  / Another App
  / Another App iOS
  / Another App Android
```

In Eclipse, import the existing projects into the workspace

1. android-support-v7-appcompat *
1. Your App
1. Your App Android *
1. FluidFramework
1. FluidFrameworkAndroid *

* These should be imported as Android projects

In Eclipse, add the following variables.

Variable | Value
-------- | ------
FLUID_FRAMEWORK_APP_IOS | ${path to "Your App IOS"}
FLUID_FRAMEWORK_APP_ANDROID | ${path to "Your App Android"}

In XCode, setup Your App iOS

#### Project folder structure

Within your project folder (in this case "Your App")

```
/ Your App
  / src
  / src-test
  / make
  / resources
    / generated
    / images
    / objc
    / settings
       settings.txt
       settings@ios.txt
    / sql
    / views
      / components
      / screens
      / table-layouts
```

settings.txt is the FluidM's application settings file. It is in [KVL](#kvl-format) format.

folder | description
------ | -----------
src | Your application code
src-test | Your unit testing code
make | Python scripts described below
resources | All the resources for your app
generated | Where FluidM places generated resources
images | Your app's images
objc | Contains prefixes.properties, used by J2ObjC to use Objective-C prefix conventions for classes instead of java packages. This file needs to be present at run time in order for reflection to work.
settings | Contains settings for different configurations such release, dry-run, development, etc.
sql | Your app's sql files
views | Your app's view files
components | Component views
screens | Screen views
table-layouts | Views layed out using the FluidM table layout

#### Python scripts

There are several scripts used to [Auto Generate](#auto-generate-scripts) things. More details are provided in the forementioned link.

file | description
--- | ---
make.python | Runs De-Lombok and J2ObjC. You need to setup the variable FLUID_FRAMEWORK_APP_IOS in Eclipse, in the same way as other variables described under [Installation](#installation). If you have more than one application in Eclipse using the framework, you should edit this file to add a new variable to replace FLUID_FRAMEWORK_APP_IOS.
makeDatastoreClasses.python | Creates java code for each table. No action necessary.
makeDatastoreVersions.python | Creates a list of versions for each database. No action necessary.
makeImagesFile.python | Creates a list of images by image resolutions, in a special format for FluidM. No action necessary.
makeResources.python | Copies resources to your iOS and Android app. You need to setup the variables FLUID_FRAMEWORK_APP_IOS and FLUID_FRAMEWORK_APP_ANDROID in Eclipse, in the same way as other variables described under [Installation](#installation). If you have more than one application in Eclipse using the framework, you should edit this file to add new variables to replace FLUID_FRAMEWORK_APP_IOS and FLUID_FRAMEWORK_APP_ANDROID.
makeViewsFile.python | Creates a list of views used by FluidM.
makeViewsFile.python | Creates a list of views used by FluidM.
makeScreenClass.python | Creates constants to screens and views that can be referenced from source code

## Developer Guide

### Data Model

The data model in FluidM is object oriented and uses dot delimited keys. An app registers a root object with a key. The object can be retrived with that key, or an object's property can be retrieved with a subkey. The subkey uses standard Java getter methods. For example, if the property is 'author', it will try 'getAuthor' and 'isAuthor' (in Java, getters are prefixed with 'get' by default, except for boolean getters, which are prefixed with 'is'). 

#### Registering data models

Data Models should be registered with FluidM so that they are accesible to the UI. This will allow the UI to query values, and also be notified whenever there are changes to the data model. Data models can be registered by calling the setDataModel method of the application. The data model is registered with a key, and the object.

Your application can register several models at the root level, or just your application itself if your application has references to all the data models in your domain.

```java
class SampleApp extends FluidApp {
  
  Library libary = new Library();

  Users users = new Users();

}

public class DataModelInitializer implements ApplicationInitializer {

  public void initialize(FluidApp fluidApp) {
    fluidApp.setDataModel("app", fluidApp);
  }

}
```

Now Library and Users can be retrived using the keys 'app.library' and 'app.users' respectively.

#### Retreiving data model values

The lookup is recursive, meaning that a sub-subkey will retrieve a property from the object referenced by the subkey.

For example,

```java
class Libarary {
  Book book;
}

class Book {
  String author;
  int numPages;
}
```

Register the library as a data model

```java
Book book = new Book();
book.author = "Herman Melville";
book.numPages = 635;

Library libarary = new Library();
library.book = book;

fluidApp.setDataModel("library", library);
```

Retrieve a value from the book, conceptually
```java
// Returns 'Herman Melville'
String author = fluidApp.getDataModelManager().getValue("library.book.author");
```

If an object in the chain is a list or an array, then the key will be treated as index into that array. Consider the following example.

```java
class Libarary {
  Book[] books;
}

Library libarary = new Library();

Book book = new Book();
book.author = "Herman Melville";
book.numPages = 635;

Book book2 = new Book();
book.author = "Earnest Hemingway"
book.numPages = 127;

library.books[0] = book;
library.books[1] = book2;
```

Retrieve a value from the book, conceptually
```java
// Returns 'Earnest Hemingway'
String author = fluidApp.getDataModelManager().getValue("library.book.1.author");
```

The API adds a few arguments to the getValue method. The first argument is a prefix. This is useful if you are iterating over a collection of objects. In our example, suppose the prefix is 'library.book'. Iterating over the book array, we could retrieve the author of each book. This would be handy if we are displaying books in a table layout, and want to display the author in each table row cell.

```java
String prefix = "library.book";

for (int index = 0; index < library.books.length; index++) {
  String author = fluidApp.getDataModelManager().getValue(prefix, index + ".author");
}
```

Or more specifically, if we are creating the cell for row 1

```java
String cellPrefix = "library.book.1";

cell.author = fluidApp.getDataModelManager().getValue(cellPrefix, "author");
cell.numPages = fluidApp.getDataModelManager().getValue(cellPrefix, "numPages");
```

The second added API argument to the getValue method is for formatting. This uses Java's [MessageFormat](http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html). From the Java API docs, "MessageFormat provides a means to produce concatenated messages in a language-neutral way. Use this to construct messages displayed for end users."

The MessageFormat is useful for constructing sentances, or formatting numbers, dates, times, currencies, percents, etc.

Suppose one view displays a floating point value with single digit precision, while another view wishes to display the value with double digit precision. Both of these views can use the same method call, but just pass in a different message format.

In the following example, a sentance is used to format the message. In the second case, the price of the book is formatted into currency, which can be customized for a user's locale.

```java
fluidApp.getDataModelManager().getValue(prefix, "numPages", "The number of pages is {0}");
fluidApp.getDataModelManager().getValue(prefix, "price", "The price of the book is {0,currency}");
```

The MessageFormat may be null, in which case the value itself is returned.

The third added API argument to the getValue method is for the case that the value is null. It is useful to display something else to the user. If the message cannot be formed because the value is missing, then the default value will be returned.

For example, suppose that we are taking a measurement of the temperature, but it takes a few seconds to measure it. In the mean time, we want to display '???'

```java
// If temperature is not available, then instead display '???'
fluidApp.getDataModelManager().getValue(prefix, "temperature", "{0} C", "???");
```

Since the MessageFormat can take more than one parameter, it would be convenient if the method could actually take in a list of keys, rather than just one key. This is supported. The key can be a CSV list.

For example, suppose we want to display a postcode as '2094 - Fairlight' or '94109 - San Francisco'

```java
class Postcode {
  int code;
  String name;
}
fluidApp.getDataModelManager().getValue(prefix, "code, name", "{0} - {1}");
```

Putting it all together, the method signature of the getValue method is

```java
public String getValue(String prefix, String keys, String messageFormat, String defaultText)
```

If you are using FluidM's [View Layout](#view-layout), you might never need to call this method, as the iOS and Android framework code will call this for you.

#### Notifying of Data Model changes

Whenever you change a value of an object that is part of your registered data model, you should notify the framework. This ensures that objects in the UI that get their data from your data model are updated.

For example, suppose your app has just received a list of books in the library from the server.

```java
public void asyncListOfBooksReceived(JsonValue list) {
  
  fluidApp.getLibrary().clearBooks();
  
  for (JsonValue bookAsJson : list.values()) {
    Book book = parseBook(bookAsJson);
    fluidApp.getLibrary().addBook(book);
  }

  fluidApp.getDataModelManager().dataDidChange("app.library");
}
```

Any registered listener of 'app.library' will be notified.

Suppose you just change the value of one book. You may have a single row in a table that displays the attributes of the book. You don't want the entire table or other rows to re-render in this case. You can be more specific with your notification.

```java
public void bookChangedName(int index, String name) {
  
  Book book = fluidApp.getLibrary().getBooks[index];
  book.name = name;

  fluidApp.getDataModelManager().dataDidChange("app.library.book." + index);
}
```

Now only listeners to that specific book will be notified.

Suppose you just change one or two values of the book. You may want your notification to be focused even more. The method takes a second argument, which is a variable argument list of subkeys. The previous example can be re-written as:

```java
public void bookChanged(int index, String name, int numPages) {
  
  Book book = fluidApp.getLibrary().getBooks[index];
  book.name = name;
  book.numPages = numPages;

  fluidApp.getDataModelManager().dataDidChange("app.library.book." + index, "name", "numPages");
}
```

Now only listeners of that specific book, book's name, or book's numPages will be notified. If there was listener of that book's price, it would not be notified.

#### Listening to Data Model changes

If you are using FluidM's [View Layout](#view-layout), you might never need to register a listener of data model changes. 

To add a change listener, specify the prefix, subkeys, listener id, and pass in a DataChangeListener. The DataChangeListener's dataChanged method will be notified whenever the key is notified.

The remove the listener, such as when a screen is no longer visible, the removeDataChangeListener method can be used.

```java
// Register a listener to receive notifications whenever this book's author changes
fluidApp.getDataModelManager().addDataChangeListener("app.library.book.0", "author", "myListenerId", new DataChangeListener() {
  public void dataChanged(String key, String...subKeys) {
    // Author changed for my book!
  }
})

// Register other listener's with id 'myListenerId'
...

// Remove the listener
fluidApp.getDataModelManager().removeDataChangeListener("myListenerId");
```

### View Layout

FluidM View Layout is optional. FluidM can be used without using the framework's view layout ecosystem. However, doing so will give you several advantages:

* The layout files are text files, just like source code
  * Diffs are meaningful in the terminal, on github, in merge tools, etc
* Screens can be specified in an OS agnostic way
* The layout format is fluid, meaning that it will adapt to the
  * screen size, and
  * device orientation
* The layout model understands the FluidM data model
  * References to the data model can be specified conveniently
* Gives you one consistent way to specify view layouts, which means less context switching

In most cases, a screen layout file can be the same on both iOS and Android. But there are cases where the user experience would suffer tremendously without customizing for the specific operating system. FluidM provides an easy mechanism to specify a different layout for a specific OS.

#### The View Layout File Format

The layout file format takes a balanced approach between visually describing the layout, and specifying detailed attributes for each element

A layout file is broken into 2 sections

1. High level layout of the elements
1. Detailed attributes for each element

For example, suppose we have an application that measures wind speed. We want the screen to display the mean and actual wind speed, and a graph of these over the several minutes. The screen should also have a start button, which should trigger the measurement to start when clicked.

Below is an example of how the high level layout is specified. Just from how the layout is specified in the file, you have an idea of how it will be positioned on the screen.

```
layout:
  -> mean                         actual
  |
  -> meanValue               actualValue
  
  |
  
  ->               graph
  
  |
  
  ->               start
```

Further down in the file, the views are described (just a few for the example).

```
views:
  mean
    type:
      label
    text:
      MEAN
    w: 
      .5 of parent
    h: 
      3

  meanValue
    type:
      label
    text: 
      {0,number,0.0}
    key:
      app.measureManager.mean
    unknown-text:
      ?
    w: 
      1 of view mean
    h: 
      7

  start
    type:
      button
    text:
      Start
    w: 
      15
    h: 
      5
```

What's really nice about this format is that the attributes for the views can be specified in as much detail as necessary, but this is separated from how they relate to each other on the screen. The 'layout' section makes it intuitive as the developer to understand how everything will be layed out. The 'views' section makes it possible to specify as many details as necessary.

##### Orientations

The first layout provided is used by default in both orientations. If 'layout-landscape:' is specified, then 'layout:' will only apply to the Portrait orientation and the 'layout-landscape:' will only apply to the Landscape orientation.

Example, which hides B in landscape orientation

```
layout:
  -> A
  
  |
  
  -> B
  
  |
  
  -> C

layout-landscape:
  -> A
  |
  -> C
```

##### Specifying a different file for a Specific Device

By default, a layout will apply to all devices. To override behavior for a specific device, create another layout file with the same name, but append '@ios' before the file extension for iOS, or 'android' for android.

For example, if the default layout is "Home.txt", an Android specific layout is "Home@android.txt"

##### Layers

The format allows layers to be specified. A new layer starts when * is placed in the layout section in place of |. The new layer will be placed above any layers before it. The anchor for the new layer can be placed with absolute coordinates, or can be placed relative to an existing view.

example

```
layout:
  -> product-image           description
  |
  <-                               price
  *
  -> promo-sticker

views:
  ...
  promo-sticker
    type:
      image
    image:
      promo-sticker
    x: 
      right of view product-image
    y: 
      top of view product-image
    w: 
      3
    h:
      3
```

##### Units

The view layout format uses a unit for sizes. 1 unit = 1 mm. FluidM takes care of this for you across different devices and resolutions. Units are nice to use because style guides typically specify things in units, for example, the minimum tappable target for a user should be 7x7 mm.

##### Anchor

The first view in a layout (or layer) is considered the anchor. It can have an X and Y coordinate specified. By default, it is 0,0. All other views are relative to the view that precedes it, therefore X and Y may not be specified.

##### How to specify Widths and Heights

By default, a width or height is fixed in terms of [Units](#units). 

If you append 'p' to the value, then it will interpreted as pixels, which scale according to the device's pixel density. For example: 5p
If you append 'pa' (think pixels actual) to the value, then it will be interpreted as that many pixels exactly, regardless of the device's pixels density. This is useful for creating thin lines and borders. For example: 1pa

You can specify dynamic lengths as well:

Length | Description
-------|------------
fill | fills the remaining space in the width or height
equal | makes the width equal to others in that row that also specify equal
ratio of another view | specified with a number, the words 'of view' and the id of the row. For example, '.5 of view A', will set its legth to 50% of the lenght of view A
ratio of parent | specified with a number, and the words 'of parent'. For example, '.5 of parent', will set its length to 50% of the length of its parent (usually the window)

##### How the layout is specified

The layout is parsed one row at a time. The parser moves in the direction specified at the start of the line. For example, if the line starts with 
->' (moving right), the parser starts in the top left corner, and moves left to right. When it reaches the end of the row, it moves down. 

A row must be prefixed with ->, |, or <-

```
-> is for rows moving left to right
| is to specify you are moving down
<- is for rows moving right to left
```

If a line starts with ->, the first element will be positioned relative to the left most element in the line above it. The subsequent elements in the line will be positioned relative to the element that preceded it. In other words, a view is positioned to the right of the previous view, meaning it's X is the previous view's X2. By default, views will be vertically aligned with top of the previous view, meaning it's Y is the previous view's Y. It can instead use bottom alignment if '(bottom)' is specified between views.

If the line starts with <-, the last element will be positioned relative to the right most element in the line above it. The preceding elements in the line will be positioned relative to the element that succeeds it. On other words, a view is positioned to the left of the previous view, meaning it's X2 is the previous view's X. The vertical alignment is the same as above.

##### How the parser resolves widths and heights

The parser works row by row. Widths are determined first. Each row must resolve its widths before the widths of the rows below. If the parser is unable to determine the width of an element in the row, then an error will be raised. The parser then goes row by row again to determine the Heights. This is made with a double pass. If at the end of the second pass any heights can't be determined, an error will be raised.

##### Built in UI view types

All types listed below support the attributes:
* background-color
* key - used to link the view to the data model. When linked, the value of the view will be set using the key, and the view will automatically listen to data model changes.

Type | Description
-----|------------
button | A button. Some supported attributes: <ul><li>text</li><li>text-color</li></ul>
image | An image. Some supported attributes: <ul><li>image</li><li>fill</li><li>condition - [condition](#condition) to which the image will be visible or hidden</li></ul>
label | A text label. Some supported attributes: <ul><li>text</li><li>text-color</li><li>align</li><li>vertical-align</li><li>font-size</li></ul>
searchbar | A search bar. Some supported attributes: <ul><li>text</li><li>text-color</li><li>show-cancel-button</li><li>placeholder-text</li></ul>
space | A visual space.
subview | A subview, which is a fluid layout component. Some supported attributes: <ul><li>subview - id of fluid component</li></ul>
table | A table. Some supported attributes: <ul><li>row-height - can be specified with row-layout to improve rendering performance</li><li>row-layout - id of fluid component</li><li>table-layout - id of fluid table-layout</li></ul> Only one of (row-layout, table-layout) can be specified. If row-layout is specified, every row in the table will use this fluid layout component. If table-layout is specified, the table the rows will be layed out according to the fluid layout table-layout.
textfield | A text field for user input. Some supported attributes: <ul><li>label</li><li>keyboard - (default, email, url, number, phone)</li><li>border-style - (default, none)</li><li>dismissKeyboardWithTap</li><li>multi-line</li><li>auto-correct</li></ul>
webview | A webview rendered from html in the binary. Some supported attributes: <ul><li>html</li></ul>
url-webview | A webview rendered from a url. Some supported attributes: <ul><li>url</li></ul>
segmented-control | A segmented control. Some supported attributes: <ul><li>array of options</li><li>color</li><li>selected index key</li></ul>

##### Table layout by row-layout

There are 2 ways FluidM lays out a table. Using a row-layout, each row is layed out the same way, using the component referred to by the row-layout. The row height should be specified on the table, in order to improve performance. Otherwise, the height will be calculated for each row when the table is rendered, which will cause the user to notice a delay in rendering for tables with 500+ rows. If the row height is dynamic, it is recommended to use [Precomputed View Positions](#precomputed-view-positions)

##### Table layout by Table-Layout

This is the second type of table layout supported by FluidM. Each row's layout is manually specified in the corresponding table-layout. One of the best uses of this format is creating forms. The format for a table-layout file is specified in [KVL](#kvl-format) format. The required sections are:

* **sections:**, each section is an id, and has **rows:**, each row is a row id
* **section-headers:**, each section-header id is referred to from 'sections:', and has the same content as a fluid layout
* **rows:**, each row id is referred to from 'sections:rows:', and has the same content as a fluid layout

sections example:

```
sections:
  quote
    rows:
      what
      where
      when
      description
  contact
    rows:
      name
      email
      phone
  another
    rows:
      submit
```

section-headers example:

```
section-headers:
  quote   
    properties:
      tablerow
        height:
          5
    background-color:
      separator-background
    layout:
      -> label
    views:
      label
        type:
          label
        text:
          QUOTE
        w:
          fill
        h:
          3
```

rows example:

```
rows:
  what
    properties:
      tablerow
        height:
          7
    background-color:
      screen-background
    layout:
      -> what   more
    views:
      what
        type:
          label
        text:
          {0}
        key:
          app.quoteForm.what
        unknown-text:
          What:
        font-size:
          3         
        align:
          left
        vertical-align:
          middle          
        w:
          fill
        h:
          7
      more
        type:
          label
        text:
          >
        align:
          right
        vertical-align:
          middle          
        w:
          3
        h:
          1 of view what
```

##### Precomputed View Positions

When rendering tables with many rows, computing the row height dynamically can be slow enough that the user will notice. In order to avoid choppy scrolling, row heights can be precomputed. In fact, any view on any screen can be precomputed to provide location (x, y) as well as size (width, height). The computations can be stored locally on the device, enabling fast look up, which provides the same performance as if the row height was specified statically.

##### View Layout Best Practices

In the layout section

* Right (->), Down (|), and Left (<-) should all line up
* The first view should start 1 space to the right of the direction indicator
  * Next views on the row should be spaced apart a minimum of 3 spaces
  * Line up the views with spaces so they visually correspond to the actual layout
* Give views meaningful names, but use s1, s2, s… for spaces

In the views section

* Put space views at the end
* Put non-space views at the top, in the order they are listed in the layout section
* Put layers in order

##### Menus

FluidM provides menus that use platform specific buttons, such as the 'camera' button or a 'sharing' button

### Handling User Events

User events, such as tapping on a button, changing the text in a search bar, or cancelling an action, can be handled gracefully by your application using an ActionListener.

An ActionListener provides the following methods which can can be implemented

* userTapped(Object userInfo)
  * called when the user taps on the associated view
  * userInfo can convey additional information, such as the row index of a table
* userChangedValueTo(Object value)
  * called when the user changes the value of the associated view, such as a searchbar or textfield
  * value is the value the user changed it to
* userCancelled
  * called when the user cancelled the action

The ActionListener can be a dedicated class, or an anonymous class (written in line). The ActionListenerAdapter is handy if you just want to implement one of the functions on the ActionListener interface.

For example, suppose that on the Home screen, you want to change to a login screen when a user taps on a Login button.

```java
app.addActionListener("Home", "Login", 
  new ActionListenerAdapter() {
    public void userTapped(Object info) {       
      fluidApp.getUiService().pushLayout("Login");
    }
  });
```

For another example, suppose that on the post codes screen, you want to change the table of results when the user changes the value in the search field.

```java
app.addActionListener("PostCodes", "search", 
  new ActionListenerAdapter() {
    public void userChangedValueTo(Object value) {
      userSearchedFor(value);
    }
  });
```

### Datastore

The FluidM Datastore ecosystem provides

* API for interacting with sqlite
* Manages database versions on the device
* Provides a standard interface and implementation that works for both iOS and Android
* Provides a mechanism to upgrade a user device's database to the current version, from any previous version
* Provides a mechanism to inject code during a specific version upgrade
* Cursor approach to iterating over the dataset (can fetch a subset of the results, and FluidM will automatically query the database for more results as necessary)

#### Sql files

As a developer, your write sql files for creating and updating your schema. FluidM takes care of the rest.

The sql files are placed in the '/resources/sql' folder. The file name needs to follow the specific format of '${database name}_(create or upgrade)_xx_yy.sql' where xx is the version number and yy is the subversion number. For example, the file to create the database 'app' which starts at version 1.1, is named app_create_01_01.sql. The file to upgrade the database 'app' to version 1.2, is named app_upgrade_01_02.sql.

When sql files are added to or modified in the '/resource/sql' folder, FluidM creates a Java class for each table. The class has constants for the table's name and column names. These constants will be picked up in your IDE, giving autocompletion. When writing sql inserts, updates, and queries, it is recommended to reference these constants, so that the IDE can provide compile time checking. The class files also have getters and setters for the table's columns. The getters and setters are Java value typed. FluidM also creates a class called DS. This class contains constants to database names. FluidM also creates a versions list for each database. This list is used by the framework to manage upgrading a user's databases.

#### Interacting with the Datastore

The framework takes advantage of Java [Generics](http://docs.oracle.com/javase/tutorial/java/generics/). This means that when writing code, the compiler is aware is of the _type_ of class being passed into a function, giving stronger compile time checking. When a value or a list of values is returned from such a function, the compiler and the developer will know which _type_ is being returned. This is very convenient and eliminates some kinds of coding mistakes.

For example, suppose that I am making a query to a customer table, represented by the class DSCustomer. By passing the class DSCustomer into the query, a set is returned that is parameterized with that class. Meaning I can iterate over the results without needing to cast the returned object into this type of class. FluidM knows how to query the database when you make a query in this way, and the framework knows how to construct an object that represents the database table.

```java
DatastoreTransaction txn...
for (DSCustomer customer : txn.query(DSCustomer.class).execute()) {
  customer.getName();
}
```

FluidM uses the [Builder](http://en.wikipedia.org/wiki/Builder_pattern) pattern for making queries, updates, and inserts. This is handy pattern to make the code more readable.

##### Queries

For queries, any FluidM object can be queried with a transaction. FluidM will write the applicable sql and perform the query.

For queries, the API offers

Method       | Parameters     | Description
-------------|----------------|------------
select       | String...      | A variable argument list of the columns to select for the query
where        | String         | The where clause. Use '{}' for parameter names, and '?' for actual values.
param        | String, Object | The parameter name and object value to fill in the where clause. This should be called in the order they appear in the where clause.
orderBy      | String         | The order by clause
limit        | int            | The limit of results to return. By default, FluidM will fetch more results as necessary, each time only fetching this limit.
offset       | int            | The offset into the query results, matched with limit
allowRefresh | boolean        | Flag to indicate if another query should be made to fetch more results as necessary. The default is true. 

Some example queries

```java

DatastoreTransaction txn...

// Find a list of book ids where the price is greater than 24.99
float price = 24.99;
SQLResultList<DSBook> books =
  txn.query(DSBook.class)
    .select(DSBook.id)
    .where("{} > ?")
    .param(DSBook.price, price)
    .execute();

// Get a specific book's name
int bookId = 423;
SQLResultList<DSBook> books =
  txn.query(DSBook.class)
    .select(DSBook.name)
    .where("{} = ?")
    .param(DSBook.id, bookId)
    .limit(1)
    .execute();
DSBook book = books.next();
book.getName();

// Iterate over a list of books for name and numPages, only fetching 20 book at a time
SQLResultList<DSBook> books =
  txn.query(DSBook.class)
    .select(DSBook.name, DSBook.numPages)
    .limit(20)
    .execute();
while (books.hasNext()) { // Will make another query after 20 books are retrieved
  DSBook book = books.next();
  book.getName();
  book.getNumPages();
  if (someCondition) {
    break; // No need to fetch all the books from the database
  }
}
```

##### Query Joins

To make query join, FluidM provides a nice API for joining 2 or 3 tables. (It is still possible to write a query join with more tables).

```java
// Find a list of books at library 35
int libraryId = 35;
SQLResultList<SQLQueryResultTuple<DSBook, DSLibrary>> results =
  txn.queryJoin(DSBook.class, DSLibrary.class)
    .select(DSBook.class, DSBook.name, DSBook.price)
    .select(DSLibrary.class, DSLibrary.name)
    .where("{} = ? and {} = ?")
    .param(DSBook.class, DSBook.id, DSLibary.class, DSLibrary.id)
    .param(DSLibrary.class, DSLibrary.id, libraryId)
    .execute();
for (SQLQUeryResultTuple<DSBook, DSLibrary> result : results) {
  DSBook book = result.t1();
  DSLibrary library = result.t2();
}
```

##### Inserts

For inserts, any FluidM DS object can be inserted with a transaction. FluidM will write the applicable sql and perform the insert. The autoincrement id is returned. For example,

```java
DSBook book = new DSBook();
book.setLibrary(library.getId());
book.setName("The Old Man and the Sea");

long newBookId = txn.insert(book);
```

##### Updates

For updates, any FluidM object can be updated with a transaction. FluidM will write the applicable sql and perform the update.

For updates, the API offers

Method       | Parameters     | Description
-------------|----------------|------------
where        | String         | The where clause. Use '{}' for parameter names, and '?' for actual values.
param        | String, Object | The parameter name and object value to fill in the where clause. This should be called in the order they appear in the where clause.

Example

```java
DSCustomer customer...
customer.setBalance(100);

txn.update(customer)
  .where("{} = ?")
  .param(DSCustomer.id, customer.getId())
  .execute();
```

##### Transactions

The recommended approach for interacting with the Datastore is to use Transactions. There are 4 steps to using a trasaction.

```java
// 1. Create the transaction for the database
DatastoreTransaction txn = new DatastoreTransaction(DS.app);

// 2. Start the transaction
txn.start();

// 3. Perform your queries, updates, and/or inserts
txn.insert(book).execute();

// 4. Commit or Rollback the transaction
txn.commit()
```

Because txn.start() will open the sqlite database, as a developer we need to ensure that we close it when we are done. Java provides try...finally blocks, such that the code in the finally block is guaranteed to run, regardless of any errors encountered inside of the try block. Therefore, the best practice, whether committing or rolling back, is to call rollback in the finally block. If a commit executes without error, then rollback will have no effect. Example,

```java
DatastoreTransaction txn = new DatastoreTransaction(DS.app);
try {
  txn.start();
  txn.insert(book).execute();
  txn.commit();
} finally {
  txn.rollback();
}
```

#### Managing Database version Upgrades

FluidM can handle most database upgrades automatically. For example, suppose you have a database called 'app', and there are versions:

```
1.0
1.1
1.2
1.3
2.0
2.2
```

If the user has the 'app' database on their device at version 1.2, and launches the application, then FluidM will apply upgrades 1.3, 2.0 and 2.2.

Most of the time, upgrades can be handled just through sql statements. But sometimes, data must be moved. In those cases, you can write an UpgradeListener. The upgrade listener will be called whenever a database is upgraded (or created). 

For example, let's say that you have added a new table to version 2.2. The new table should contain a list of postcodes, which you can populate from a csv file. You can create an upgrade listener to populate this table after the database has been upgraded to the version containing the new table.

Example,

```java
public class UpgradeListener_02_02 implements UpgradeListener {

  public boolean databaseWasUpgraded(DatastoreVersion version) {

    Csv csv...

    DatastoreTransaction txn = new DatastoreTransaction();
    txn.start();

    for (Row row : csv) {
      DSPostcode postcode = new DSPostcode();
      postcode.setCode(row.get("code"));
      postcode.setTitle(row.get("title"));

      txn.inset(postcode);
    }
    txn.commit();
  }
}
```

##### Registering an UpgradeListener

UpgradeListeners must be registered during application initialization. A listener can be registered with the DataStoreManager, specifying the datastore version it is listening for.

```java
fluidApp.getDatastoreManager().setUpgradeListener(new DatastoreVersion(2, 2), new UpgradeListener_02_02());
```

##### Upgrade under the hood

FluidM keeps a special table of properties within the database. This table contains the current database version. If the version in the database is behind, then FluidM will make a backup copy of the database. Then FluidM will apply each upgrade, update the database version, and then commit the result. If something goes wrong during the upgrade, then the current database will be discarded, and the backup will be restored.

### Images

FluidM mechanism to choose the right image for the screen resolution.

Place all your images in 'resources/images'

Image files should be named as ${base name}@(a,b,c,...).png

FluidM will build a list of image resolutions per base name.

For example, if I have sailboat.png, I should provide

```
sailboat@a.png with resolution 48x48
sailboat@b.png with resolution 64x64
sailboat@c.png with resolution 96x96
sailboat@d.png with resolution 144x144
...
```

When asking the ImageManager for an image by base name, and specifying the dimensions, the full name of the best image to use will be returned.

```java
// Returns "sailboat@c.png"
fluidApp.getImageManager().getImage("sailboat", 90, 90);
```

If you refer to an image in a View Layout file, the framework will automatically call this with the calculated dimensions for the view and use the appropriate image.

### File Resources

FluidM provides a service for loading resources from the file system in the OS appropriate way.

To use the Resource service, pass in the directory and name of the asset. For example,

```java
String csvContents = fluidApp.getResourceService().getResourceAsString("csv", "postcodes.csv");

// or get the actual bytes
byte[] data = fluidApp.getResourceService().getResourceAsBytes("raw-data", "somefile");
```

### Logging

FluidM provides a logging mechanism, with support for logging levels, which does the appropriate thing for the OS it is running on.

The Logger provides 4 logging levels. The level may be set in /resources/settings.txt

```
debug
info
warn
error
```

Logging levels cascade - If the logging level is set at debug, you will get logging messages for all levels. If the logging level is set at warn, you will only get logging messages for warn and error.

For each level, you can give a logging message, or a Throwable exception. The latter will log the stack trace.

Each logging method takes the following arguments

Argument  | Type      | Description
----------|-----------|------------
thisClass | Object    | The object throwing the exception
msg       | String    | The logging message to display. Any variables should be indicated with {}
params    | Object... | Variable list of variables to match up to the {} in msg

Example

```java
Logger.debug(this, "Calling someOp");
int result = someOp();
if (result != 0) {
  Logger.warn(this, "Result from someOp was not successful, it was {}", result);
} else {
  Logger.debug(this, "someOp success");
}

try {
  anotherOp();
} catch (SeriousException e) {
  Logger.error(this, e);
}
```

### Http Service

FluidM provides an http service for connecting to a URL via Get, Post, Put, and Delete. The connection is made asynchrounously. An HttpServiceCallback is passed which will be notified when the action is complete.

An HttpAuthorization object may be passed in order to use http authentication.

Example

```java
String url = "https://api.forecast.io/forecast/0eda8b99b6e311d2f3c62aa8eb77cad4/-33.86,151.2111";
HashMap<String, Object> parameters...
HttpAuthorization auth...

fluidApp.getHttpService().get(url, parameters, auth, 
  
  new HttpServiceCallback() {
    
    public void success(HttpResponse response) {
      if (response.getCode() == 200) {
        String value = response.getData();
      }
    }
  
    public void fail(HttpResponse response) {
      // Request failed
    }
  }); 
```

#### JSON

FluidM provides some utilities for parsing data in JSON format. The framework uses a fast and minimal [JSON parser](https://github.com/ralfstx/minimal-json). (Which has been translated to Objective-C)

Example

```java
// Read JSON from a String
JsonObject jsonObject = JsonObject.readFrom( string );
JsonArray jsonArray = JsonArray.readFrom( string );

// Access the contents of a JSON object
String name = jsonObject.get( "name" ).asString();
int age = jsonObject.get( "age" ).asInt();
```

FluidM also provides a utility, JsonUtil, which wraps a JsonObject, and transfers the values to a plain Java object using standard setter methods. In this way, the developer doesn't have to call the getters of a JsonObject for each property on the Java object.

Example

```java
JsonObject json...
WeatherDataPoint dataPoint = new WeatherDataPoint();
JsonUtil.setValuesTo(dataPoint, json);
```

### UI Service

FluidM provides a UI service for interacting with the user interface on the device.

The API offers

Method             | Parameters      | Description
-------------------|-----------------|------------
pushLayout         | String screenId | Pushes a screen onto the view stack, and makes that screen the current view
popLayout          |                 | Pops the current screen onto the view stack, and makes the previous screen the current view
setLayout          | String          | Sets the screen as the current view, switching tabs if necessary (should be removed with closeCurrentLayout)
setLayoutStack     | String Array    | Sets a stack of views in the view hierarcy, with the top screen as the current view (should be removed with closeCurrentLayout)
showModalView      | ModalView       | Sets a modal view as the current modal view (should be dismissed with dismissModalView). Currently supported modal views are: ImagePicker, Another FluidM Screen
dismissModalView   |                 | Dismisses the current modal view
closeCurrentLayout |                 | Closes the current view
showAlert          | String title, String message | Shows an alert to the user with the title and message

### Application Initializiation

An application's main class extends from the abstract base class FluidApp.

When a fluid application is constructed, it has a chance to register ApplicationInitializers and ApplicationLoaders. Then when the application starts, the initializers, and then loaders, are ran in sequence. 

In fact, all of the initialization within the framework is done through ApplicationInitializers and ApplicationLoaders.

ApplicationLoaders should be used by default. ApplicationInitializers should be used for things that need to happen before the application's splash screen is shown.

The final ApplicationLoader should invoke UIService.removeSplashScreen. The core app will then invoke startApp()

Whenever an application restarts by resuming from the background, reStartApp() will be invoked

What an application starts up, the following steps are performed.

1. Base class FluidApp is constructed
1. FluidApp ApplicationInitializers and ApplicationLoaders are added
  * SettingsParser - parses application settings (resources/settings.txt)
  * LoggingInitializer - configures the logging system
  * ViewParser - parses the views (resources/views/)
  * TabParser - parses and configures the tabs in a tabbed application
  * ViewManager - ensures the base unit was set
  * ImageManager - loads the image resolutions (resources/generated/images.txt)
  * DatastoreManager - created or upgrades databases
1. Application is constructed, but shouldn't perform any initialization yet
1. Application registers its initializers and loaders
1. Native code sets
  * The platform (iOS or Android)
  * The HttpService implementation
  * The ResourceService implementation
  * The DatastoreService implementation
  * The base unit size in pixels for the device
1. The application is started
  1. All initializers are ran, in the order they were added
  1. Application splash screen is shown
  1. All loaders are ran, in the order they were added
  1. Splash screen is removed
  1. The initializers and loaders are dumped from memory (although some stick around like the DatastoreManager)
  1. startApp() is called, which is implemented by the Application
    * This is where the application startup code should be placed, ie. connecting to server, ...

When an ApplicationInitializer(Loader) is run, the initialize method is invoked. 

An ApplicationInitializer(Loader) can run or not run based on the running platform. The ApplicationInitializer(Loader) indicates this with its return value in the getSupportedPlatforms() method. If the list is null, that means it supports all platforms. Otherwise, it can return a String[] of the platforms it supports (Platforms.IOS, Platforms.Android);

Example

```java
public class DataModelLoader implements ApplicationLoader {

  public void load(final FluidApp app) {
    app.setDataModel("app", app);
  }

  public String[] getSupportedPlatforms() {
    return null;
  }

}
```

It is recommended that an application:

* Creates an ApplicationLoader for each Screen that adds ActionListeners
* Has a method called 'addApplicationInitializers' and 'addApplicationLoaders' and calls it from its constructor.
  * The addApplicationLoaders method itself can be broken into categories, such as addUILoaders()
  * The addUILoaders() function would then add the listener for each Screen

Example

```java
public SampleApp() {
  super();
  
  addApplicationLoaders();
  
  addDatastoreUpgradeListeners();
}

private void addApplicationLoaders() {
  addLoader(new DataModelLoader());
  addUIInitializers();
}

private void addUILoaders() {
  
  // Add some screens
  addLoader(new Home());
  addLoader(new Measure());
  addLoader(new GetQuoteForm());
  
  // You can add more than one at a time. It's handy to put related initializers on the same call.
  addLoader(new SearchWhat(), new SearchWhatAndroid());
}
```

## Misc

### KVL Format

Key Value List format is a flexible file format that:

* Is easy to read in plain text, compared to XML which is convoluted
* Flexible, compared to CSV in which all rows must adhere to a specific format
* Is easy to use in code

The format is great for settings, but it is not a silver bullet. For example, if your data is well suited for CSV format, KVL format would take up unnecessary space.

The format itself is recursive. Querying a KVL for a key will return another KVL. A KVL contains a list of values, each of which is a KVL, and can be queried. 

Example

```
sections:
  quote
    rows:
      what
        color:
          red
      where
        color:
          blue
      when
        color:
          green
  contact
    rows:
      name
      email
      phone
  another
    rows:
      submit
```   

The code can then get a list of sections

```java
KVLReader reader = new KVLReader("file.txt");
for (KeyValueList section : settings.get("sections")) {
  String sectionName = section.getValue();
  for (KeyValueList row : section.get("rows")) {
    String rowId = row.getValue();
    if (row.contains("color")) {
      String color = row.getValue("color");
    }
  }
}
```

### Condition

FluidM provides a conditional syntax. This is used in the UI view type image to determine if the image should be hidden or visible. 

The syntax is in the format of ${Side A} ${Condition} ${Side B}

* ${Side A} is a key to a value from the data model
* ${Condition} is one of (==, !=)
* ${Side B} can be 'true', 'false', '' (empty or null), or else a key to a value from the data model

Alternatively, ${Condition} and ${Side B} may not be provided, in which case:

* If ${Side A} is prefixed with !, then ${Condition} will use != and ${Side B} will use 'true'
* Else, ${Condition} will use == and ${Side B} will use 'true'

## License
Copyright 2014 Hans Sponberg

Code licensed under the MIT License: http://opensource.org/licenses/MIT
