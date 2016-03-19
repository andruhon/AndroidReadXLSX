#Not needed any more!
Please note that all this hacking is not needed any more with new Android Build Tools 21+  and Android 5 (ART):  
Visit this blog post for details:  
http://blog.kondratev.pro/2015/08/reading-xlsx-on-android-4-and-hopefully.html  
Generally all you need is to enable multi-dex support and build your project with --core-library option.
See also the repo with repacked POI 3.12 jars and gradle config examples: https://github.com/andruhon/android5xlsx

Please refer to the text below if you still need to maintain Android 4 (Dalvik):

#Reading XLSX on Android 4

A workaround make limited usage of Apache POI XSSF on the Android 4 possible.

Se usage example with Gradle / Android Studio in [example](example). Old Ant example can be found in [antbuild](https://github.com/andruhon/AndroidReadXLSX/tree/antbuild) branch.

This is actually not "out of box" POI chopped from something like 80K methods down to fit into 65K methods android limit.

**JARs to download:**

* [aa-poi-3.10-min-0.1.5.jar](aa-poi-3.10-min-0.1.5.jar) contains following libraries, shrunk with proguard:
* [aa-poi-ooxml-schemas-3.10-reduced-more-0.1.5.jar](aa-poi-ooxml-schemas-3.10-reduced-more-0.1.5.jar) contains ooxml-schemas, shrunk manually


**It does work without DX --core-library** option because StAX is re-compiled with 'javax' namespace renamed to 'aavax'. So namespace conflict is not possible any more.
Strings 'javax/xml/stream', 'javax/xml/namespace' and 'javax.xml.strings' in other binaries replaced with strings containing 'aavax' instead of 'javax'.

Tested with reading and writing XLSX files. It might not work properly if the file contains Drawings or Charts. It also might fail if you try to write some styles. Please let me know if it fails by any reason.

##Building the project with aa-poi-3.10-min-0.1.5.jar and aa-poi-ooxml-schemas-3.10-reduced-more-0.1.5.jar

The jars are still quite large so this might happen that project will touch 65K methods limit if one add any other jars into the project. For example android appcompat Support Library will definitely cause this issue.


##Why?
If you try to build Android project with Apache POI XSSF out of box you would face whole bunch of problems:<br />
First and most major one is android's 65K methods limit. poi-ooxml-schemas-3.10-FINAL-20140208.jar contains approximately 68K
```
([dx] trouble writing output: Too many method references: 67997; max is 65536.)
```

Read following posts in my blog to see whole process of "porting":
* http://blog.kondratev.pro/2014/08/reading-xlsx-on-android.html
* http://blog.kondratev.pro/2014/09/further-to-my-post-from-yesterday-on.html
* http://blog.kondratev.pro/2014/09/reading-xlsx-on-android-3.html

##Known issues
It fails to create a new XLSX file with a new sheet. Workaround is in keeping pre-created blank XLSX in app assets and using this file as templates. Populating/writing existing files with existing sheets works fine. Find workaround example in [MainActivity.java:onWriteClick](example/app/src/main/java/pro/kondratev/xlsxpoiexample/MainActivity.java)

##Donate / help
I don't ask for a donation, but you can join me on the LinkedIN in and endorse my Java skill if you find this hack useful:
https://nz.linkedin.com/pub/andrei-kondratev/51/445/635
