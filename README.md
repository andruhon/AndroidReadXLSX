#Reading XLSX on Android

Proof of possibility to use Apache POI XSSF on Android to read XLSX files.

If you try to build Android project with Apache POI XSSF out of box you would face whole bunch of problems.
First and most major one is android methods 65K methods limit.
poi-ooxml-schemas-3.10-FINAL-20140208.jar contains approximately 68K
```
([dx] trouble writing output: Too many method references: 67997; max is 65536.)
```

The core idea is just to remove from poi-ooxml and poi-ooxml-schemas jars all files which are seems to be not necessary to read XLSX.
For example xslf and xwpf from poi-ooxml,
schemas presentationml and wordrpocessingml from poi-ooxml-schemas
and other stuff...

After that just unpack full ooxml schemas pack ooxml-schemas-1.0.jar
and add files/classes one by one into your poi-ooxml (using zip) as exceptions thrown by you app when you trying to read xlsx.

This project already contains two reduced jars:
* **libs/poi-ooxml-3.10-reduced.jar**
* **libs/poi-ooxml-schemas-3.10-reduced-more.jar**
these two are enough to read simple XLSX files with formulas and some formatting.
All other jars are obviously should be included too, but they are not modified.


Tested with reading only, did not tested with writing yet.

Write me on andruhon@gmail.com if you can suggest different approaches of running XSSF on android 


##Building project
DX should be called with --core-library to build this project, one can modify dx.bat and add --core-library before %params%
--core-library option is needed because poi-ooxml has stax-api in its dependences.
stax-api-1.0.1.jar contain java core libs which are not included in adroid.jar though.

You can use ant to build this project just do:
>ant clean
>ant build
the project already contains build.xml,

If you'd like to use reduced libs in your project do:
>android update project --path .
to initialize ant in your project dir