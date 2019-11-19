default:
	javac -h . HSL.java
	gcc hsl.c -shared -O3 -I/usr/lib/jvm/java-11-openjdk-armhf/include -I/usr/lib/jvm/java-11-openjdk-armhf/include/linux -o libhsl.so

clean:
	rm org_aluminati3555_aluminativision_hsl_HSL.h
	rm HSL.class
	rm libhsl.so

install:
	cp libhsl.so ~/AluminatiVision/jni
