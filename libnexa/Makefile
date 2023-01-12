all: ./build/android/${ARCH}/libnexalight.so ./build/android/${ARCH}/libnexalight.a

nexa/configure: nexa/configure.ac
	echo configure not needed because manually inserting nexa-config.h
	# (cd nexa; ./autogen.sh)

nexa/${TARGET}: nexa/configure
	mkdir -p nexa/${TARGET}


nexa/${TARGET}/config.status: nexa/configure
	mkdir -p nexa/${TARGET}
	# not needed because nexa-config.h provided
	# (cd nexa/${TARGET}; ../configure --host=${TARGET} CFLAGS=-fpic --disable-bench --disable-tests --with-gui=no --disable-wallet --enable-shared)

nexa/${TARGET}/src/.libs/libnexa.so: nexa/${TARGET} nexa/${TARGET}/config.status
	echo ${CC} ${CXX}; make -C nexa/${TARGET} V=1

nexa/src/cashlib/nexa-config.h: nexa-config.h
	cp nexa-config.h nexa/src/cashlib

nexa/${TARGET}/lightbuild/Makefile: nexa/src/cashlib/nexa-config.h nexa/src/cashlib/CMakeLists.txt nexa/${TARGET}/config.status
	mkdir -p nexa/${TARGET}/lightbuild
	(cd nexa/${TARGET}/lightbuild; cmake ../../src/cashlib)

nexa/${TARGET}/lightbuild/libnexalight.so: nexa/${TARGET} nexa/${TARGET}/lightbuild/Makefile
	(echo ${CC} ${CXX}; make -C nexa/${TARGET}/lightbuild)

#./build/android/${ARCH}/libnexalight.a: build/android/${ARCH} nexa/${TARGET}/lightbuild/libnexalight.so
#	cp nexa/${TARGET}/src/.libs/libnexalight.a build/android/${ARCH}/libnexalight.a

./build/android/${ARCH}/libnexalight.so: build/android/${ARCH} nexa/${TARGET}/lightbuild/libnexalight.so
	cp nexa/${TARGET}/lightbuild/libnexalight.so build/android/${ARCH}/libnexalight.so

./build/android/${ARCH}/libnexalight.a: build/android/${ARCH} nexa/${TARGET}/lightbuild/libnexalight.so
	cp nexa/${TARGET}/lightbuild/libnexalighta.a build/android/${ARCH}/libnexalight.a

./build/android/${ARCH}:
	mkdir -p ./build/android/${ARCH}




clean:
	rm -rf nexa/i686-linux-android nexa/x86_64-linux-android nexa/aarch64-linux-android nexa/armv7a-linux-androideabi nexa/arm-apple-darwin

	rm -rf build

cleanall: clean
	rm nexa/configure
