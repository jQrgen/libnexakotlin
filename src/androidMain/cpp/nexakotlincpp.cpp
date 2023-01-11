#include <jni.h>

extern "C"
{

    // dummy function
JNIEXPORT jlong JNICALL Java_net_kodein_demo_crypto_libnexakotlincpp_helloworld(JNIEnv *, jobject)
{
    return 10;
}



}
