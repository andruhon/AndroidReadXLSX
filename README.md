#Reading XLSX on Android

Proof of possibility to use Apache POI XSSF on Android to read XLSX files.

libs dir of this project contains following files:
* poi-3.10-min-0.1.jar
* poi-ooxml-schemas-3.10-reduced-more.jar
* stax-api-1.0.1.jar

These jars are enough to read and write XLSX and XLS files. 

poi-3.10-min-0.1.jar contains following libraries shrunk with proguard:
*dom4j-1.6.1.jar
*poi-3.10-FINAL-20140208.jar
*poi-ooxml-3.10-FINAL-20140208.jar
*xmlbeans-2.3.0.jar

Now tested with writing too - it works. Will publish example later.

##Building project
DX should be called with --core-library to build this project, one can modify dx.bat and add --core-library before %params%
--core-library option is needed because poi-ooxml has stax-api in its dependences.
stax-api-1.0.1.jar contain java core libs which are not included in adroid.jar though.

You can use ant to build this project just do:
```
>ant clean
>ant build
```
If the project already contains build.xml,

If you'd like to use reduced libs in your project without build.xml, do:
>android update project --path .
to initialize ant in your project dir

##Why?
If you try to build Android project with Apache POI XSSF out of box you would face whole bunch of problems.
First and most major one is android methods 65K methods limit.
poi-ooxml-schemas-3.10-FINAL-20140208.jar contains approximately 68K
```
([dx] trouble writing output: Too many method references: 67997; max is 65536.)
```

The core idea is just to remove from poi-ooxml and poi-ooxml-schemas jars all classes which are seems to be not necessary to read XLSX.
For example presentationml and wordrpocessingml from poi-ooxml-schemas
and other stuff...

poi-ooxml was shrunk with proguard, poi-ooxml-schemas has been shrunk manually - because I did not find proguard config which is not braking it.

After that just unpack full ooxml schemas pack ooxml-schemas-1.0.jar
and add files/classes one by one into your poi-ooxml-schemas (using zip) as exceptions thrown by you app when you trying to read xlsx.


Write me on andruhon@gmail.com if you can suggest different approaches of running XSSF on android