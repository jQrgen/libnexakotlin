#!/usr/bin/env bash
# Try: ANDROID_NDK=/home/stone/Android/Sdk/ndk/21.4.7075529 ARCH=arm64-v8a TOOLCHAIN=linux-x86_64
set -e

[[ -z "$ANDROID_NDK" ]] && echo "Please set the ANDROID_NDK variable" && exit 1
[[ -z "$ARCH" ]] && echo "Please set the ARCH variable" && exit 1
[[ -z "$TOOLCHAIN" ]] && echo "Please set the TOOLCHAIN variable" && exit 1

echo "build-android.sh: Input environment variables to reproduce this build"
echo "ANDROID_NDK=$ANDROID_NDK ARCH=$ARCH TOOLCHAIN=$TOOLCHAIN"

if [ "$ARCH" == "x86_64" ]; then
    SYS=x86_64
elif [ "$ARCH" == "x86" ]; then
    SYS=i686
elif [ "$ARCH" == "arm64-v8a" ]; then
    SYS=aarch64
elif [ "$ARCH" == "armeabi-v7a" ]; then
    SYS=armv7a
else
    echo "Unsupported ARCH: $ARCH"
    exit 1
fi

TARGET=$SYS-linux-android
TOOLTARGET=$TARGET
if [ "$SYS" == "armv7a" ]; then
    TARGET=armv7a-linux-androideabi
    TOOLTARGET=arm-linux-androideabi
fi

export TARGET=${TARGET}
export CXX=$ANDROID_NDK/toolchains/llvm/prebuilt/$TOOLCHAIN/bin/${TARGET}21-clang++
export CC=$ANDROID_NDK/toolchains/llvm/prebuilt/$TOOLCHAIN/bin/${TARGET}21-clang
export LD=$ANDROID_NDK/toolchains/llvm/prebuilt/$TOOLCHAIN/bin/$TOOLTARGET-ld
export AR=$ANDROID_NDK/toolchains/llvm/prebuilt/$TOOLCHAIN/bin/$TOOLTARGET-ar
export AS=$ANDROID_NDK/toolchains/llvm/prebuilt/$TOOLCHAIN/bin/$TOOLTARGET-as
export RANLIB=$ANDROID_NDK/toolchains/llvm/prebuilt/$TOOLCHAIN/bin/$TOOLTARGET-ranlib
export STRIP=$ANDROID_NDK/toolchains/llvm/prebuilt/$TOOLCHAIN/bin/$TOOLTARGET-strip
# export CXXFLAGS=-DANDROID

echo TARGET=${TARGET}

make -j 10
exit 0

# If using cmake
# cmake .
# exit 0

cd nexa

./autogen.sh

mkdir -p $TARGET
cd $TARGET

../configure --host=$TARGET CFLAGS=-fpic --disable-bench --disable-tests --with-gui=no --disable-wallet --enable-shared

make clean
make -j10

cd ..

mkdir -p ../build/android/$ARCH
cp -v ./src/.libs/libnexa.so ../build/android/$ARCH
