JAVA := ${JAVA_HOME}/bin/java

BUILD_CLASSPATH := lib/commons-logging-1.0.4.jar

JAVASRC := $(shell find src -name '*.java')
JNI_BINDINGS_H := src/main/c/javafs_bindings.h
JNI_BINDINGS_C := src/main/c/javafs_bindings.c
JNI_COMMON_H := javafs.h

.PHONY: all jni_all clean jni_clean

all: ${JNI_BINDINGS_H} ${JNI_BINDINGS_C} jvm_ldpath.def
	${MAKE} -C jni all

clean:
	${MAKE} -C jni clean
	rm -f ${JNI_BINDINGS_H} ${JNI_BINDINGS_C}
	rm -f jvm_ldpath.def

${JNI_BINDINGS_H} ${JNI_BINDINGS_C}: ${JAVASRC}
	${JAVA} -classpath build java2c.CAPIGenerator \
	${JNI_BINDINGS_H} ${JNI_BINDINGS_C} ${JNI_COMMON_H}

jvm_ldpath.def:
	${JAVA} -classpath build java2c.DumpJVMLdPath > jvm_ldpath.def


