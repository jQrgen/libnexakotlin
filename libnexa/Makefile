lc = $(subst A,a,$(subst B,b,$(subst C,c,$(subst D,d,$(subst E,e,$(subst F,f,$(subst G,g,$(subst H,h,$(subst I,i,$(subst J,j,$(subst K,k,$(subst L,l,$(subst M,m,$(subst N,n,$(subst O,o,$(subst P,p,$(subst Q,q,$(subst R,r,$(subst S,s,$(subst T,t,$(subst U,u,$(subst V,v,$(subst W,w,$(subst X,x,$(subst Y,y,$(subst Z,z,$1))))))))))))))))))))))))))

BUILD_DIR:=$(abspath nexa/${TARGET}/lightbuild)

# platform is PLATFORM lowercase
platform:=$(call lc,${PLATFORM})
# $(warning ${platform})
all: build/${platform}/${ARCH}/libnexalight.so build/${platform}/${ARCH}/libnexalight.a

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
	echo ${CC} ${CXX}; $(MAKE) -C nexa/${TARGET} V=1

nexa/src/cashlib/nexa-config.h: nexa-config.h
	cp nexa-config.h nexa/src/cashlib

# triggers cmake to create a makefile (DEPRECATED)
nexa/${TARGET}/lightbuild/Makefile: nexa/src/cashlib/nexa-config.h nexa/src/cashlib/CMakeLists.txt nexa/${TARGET}/config.status
	mkdir -p nexa/${TARGET}/lightbuild
	(cd nexa/${TARGET}/lightbuild); cmake ../../src/cashlib)

# will build both .so and .a
nexa/${TARGET}/lightbuild/libnexalight.so:
	mkdir -p nexa/${TARGET}/lightbuild
	BUILD_DIR=${BUILD_DIR} $(MAKE) -C nexa/src/cashlib ${BUILD_DIR}/libnexalight.so

nexa/${TARGET}/lightbuild/libnexalight.a:
	@echo deferring build of $@ to cashlib
	mkdir -p nexa/${TARGET}/lightbuild
	BUILD_DIR=${BUILD_DIR} $(MAKE) -C nexa/src/cashlib ${BUILD_DIR}/libnexalight.a


#./build/android/${ARCH}/libnexalight.a: build/android/${ARCH} nexa/${TARGET}/lightbuild/libnexalight.so
#	cp nexa/${TARGET}/src/.libs/libnexalight.a build/android/${ARCH}/libnexalight.a

build/${platform}/${ARCH}/libnexalight.so: build/${platform}/${ARCH} nexa/${TARGET}/lightbuild/libnexalight.so
	cp nexa/${TARGET}/lightbuild/libnexalight.so build/${platform}/${ARCH}/libnexalight.so

build/${platform}/${ARCH}/libnexalight.a: build/${platform}/${ARCH} nexa/${TARGET}/lightbuild/libnexalight.a
	cp nexa/${TARGET}/lightbuild/libnexalight.a build/${platform}/${ARCH}/libnexalight.a

build/${platform}/${ARCH}:
	mkdir -p ./build/${platform}/${ARCH}




clean:
	rm -rf nexa/i686-linux-android nexa/x86_64-linux-android nexa/aarch64-linux-android nexa/armv7a-linux-androideabi nexa/arm-apple-darwin
	rm -rf build

cleanall: clean
	-rm nexa/configure
