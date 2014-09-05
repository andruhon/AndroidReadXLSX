#Reading XLSX on Android

Proof of possibility to use Apache POI XSSF on Android to read XLSX files.

libs dir of this project contains following files:
* poi-3.10-min-0.1.jar
* poi-ooxml-schemas-3.10-reduced-more.jar
* stax-api-1.0.1.jar

These jars are enough to read and write XLSX and XLS files. 

poi-3.10-min-0.1.jar contains following libraries shrunk with proguard:
*stax-api-1.0.1.jar
*dom4j-1.6.1.jar
*poi-3.10-FINAL-20140208.jar
*poi-ooxml-3.10-FINAL-20140208.jar
*xmlbeans-2.3.0.jar

STaX is re-compiled with 'javax' namespace renamed to 'aavax' to avoid using DX --core-library option and possible conflicts in future.
Strings 'javax/xml/stream', 'javax/xml/namespace' and 'javax.xml.strings' in other binaries replaced with strings containing 'aavax' instead of 'javax'.
**No more need in DX --core-library option!**

Now tested with writing too - it works. Will publish example later.

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