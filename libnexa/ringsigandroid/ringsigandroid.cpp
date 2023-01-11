// Copyright (c) 2021 The Bitcoin Unlimited developers
// Distributed under the MIT software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.

#include "fujisaki_ringsig.h"

#include <android/log.h>
#include <jni.h>

#include <string>
#include <vector>

namespace {

static constexpr size_t PRIVKEY_SIZE = 64;
static constexpr size_t PUBKEY_SIZE = 32;

static constexpr size_t SIGNATURE_BUFFER_SIZE = 1024 * 512; // 0.5MB

jint triggerJavaIllegalStateException(JNIEnv *env, const char *message)
{
    jclass exc = env->FindClass("java/lang/IllegalStateException");
    if (nullptr == exc)
        return 0;
    return env->ThrowNew(exc, message);
}

jbyteArray makeJByteArray(JNIEnv *env, uint8_t *buf, size_t size)
{
    jbyteArray bArray = env->NewByteArray(size);
    jbyte *dest = env->GetByteArrayElements(bArray, 0);
    memcpy(dest, buf, size);
    env->ReleaseByteArrayElements(bArray, dest, 0);
    return bArray;
}

OpaquePtr to_opaque_ptr(jlong rawPtr) {
    OpaquePtr ptr;
    ptr._0 = (void*) rawPtr;
    return ptr;
}

jlong opaque_as_jlong(const OpaquePtr& ptr) {
    return (jlong) ptr._0;
}

template <typename T>
std::tuple<size_t, T> fromByteArray(JNIEnv* env, const jbyteArray array) {
    size_t n = env->GetArrayLength(array);
    return { n, (T) (env->GetByteArrayElements(array, nullptr)) };
}

jobject newInteger(JNIEnv* env, int value){
    jclass integerClass = env->FindClass("java/lang/Integer");
    jmethodID integerConstructor = env->GetMethodID(integerClass, "<init>", "(I)V");
    return env->NewObject(integerClass, integerConstructor, static_cast<jint>(value));
}

} // ns anon

extern "C"
JNIEXPORT jlong JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_generate_1keypair(JNIEnv *env, jclass clazz) {
    OpaquePtr keypair = generate_keypair();
    return opaque_as_jlong(keypair);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_generate_1keypair_1from_1bits(
    JNIEnv *env, jclass clazz, jbyteArray seedIn)
{
    const auto& [nseed, seed] = fromByteArray<const uint8_t*>(env, seedIn);
    if (nseed != 32) {
        triggerJavaIllegalStateException(env, "Invalid seed size. Must be 32 bytes.");
        return 0;
    }
    OpaquePtr keypair = generate_keypair_from_bits(seed);
    return opaque_as_jlong(keypair);
}

extern "C"
JNIEXPORT void JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_free_1keypair(
        JNIEnv *env, jclass clazz, jlong keyPairPtr) {
    free_keypair(to_opaque_ptr(keyPairPtr));
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_get_1privkey(
        JNIEnv *env, jclass clazz, jlong keyPairPtr) {
    uint8_t buffer[PRIVKEY_SIZE];
    auto keypair = to_opaque_ptr(keyPairPtr);
    if (!get_privkey(&keypair, &buffer)) {
        triggerJavaIllegalStateException(env, "Failed to get private key from keypair");
    }
    return makeJByteArray(env, buffer, PRIVKEY_SIZE);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_get_1pubkey(
        JNIEnv *env, jclass clazz, jlong keyPairPtr) {
    uint8_t buffer[PUBKEY_SIZE];
    auto keypair = to_opaque_ptr(keyPairPtr);
    if (!get_pubkey(&keypair, &buffer)) {
        triggerJavaIllegalStateException(env, "Failed to get public key from keypair");
    }
    return makeJByteArray(env, buffer, PUBKEY_SIZE);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_init_1tag(JNIEnv *env, jclass clazz, jbyteArray issueIn) {

    const auto& [nissue, issue] = fromByteArray<const uint8_t*>(env, issueIn);

    OpaquePtr tag = init_tag(issue, nissue);
    return opaque_as_jlong(tag);
}

extern "C"
JNIEXPORT void JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_tag_1add_1pubkey(JNIEnv *env, jclass clazz, jlong tagPtr, jbyteArray pubkey) {
    size_t size = env->GetArrayLength(pubkey);
    if (size != PUBKEY_SIZE) {
        triggerJavaIllegalStateException(env, "Invalid size for pubkey argument");
    }
    uint8_t* data = (uint8_t*) env->GetByteArrayElements(pubkey, nullptr);
    auto tag = to_opaque_ptr(tagPtr);
    if (!tag_add_pubkey(&tag, data)) {
        triggerJavaIllegalStateException(env, "Failed to add pubkey to tag");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_free_1tag(JNIEnv *env, jclass clazz, jlong tagPtr) {
    free_tag(to_opaque_ptr(tagPtr));
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_sign_1message(
    JNIEnv *env,
    jclass clazz,
    jbyteArray messageIn,
    jlong tagPtr,
    jbyteArray privkeyIn) {

    auto buffer = std::make_unique<uint8_t[]>(SIGNATURE_BUFFER_SIZE);
    const auto& tag = to_opaque_ptr(tagPtr);

    const auto& [nprivkey, privkey] = fromByteArray<const uint8_t*>(env, privkeyIn);
    if (nprivkey != PRIVKEY_SIZE) {
        triggerJavaIllegalStateException(env, "Invalid size for private key");
        return makeJByteArray(env, buffer.get(), 0);
    }
    const auto& [nmessage, message] = fromByteArray<const uint8_t*>(env, messageIn);

    size_t nsig = sign(message, nmessage, &tag, privkey, buffer.get());

    if (nsig == 0) {
        triggerJavaIllegalStateException(env, "Failed to sign message");
        return makeJByteArray(env, buffer.get(), 0);
    }

    return makeJByteArray(env, buffer.get(), nsig);
}

// external fun verify_message(msg: ByteArray, tag: TagPtr, signature: ByteArray): Boolean
extern "C"
JNIEXPORT bool JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_verify_1message(
    JNIEnv *env,
    jclass clazz,
    jbyteArray messageIn,
    jlong tagPtr,
    jbyteArray signatureIn) {


    const auto& [nmessage, message] = fromByteArray<const uint8_t*>(env, messageIn);
    const auto& tag = to_opaque_ptr(tagPtr);
    const auto& [nsignature, signature] = fromByteArray<const uint8_t*>(env, signatureIn);

    return verify(message, nmessage, &tag, signature, nsignature);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_bitcoinunlimited_libbitcoincash_RingSignatureKt_trace_1signature(
    JNIEnv *env,
    jclass clazz,
    jbyteArray message1In,
    jbyteArray signature1In,
    jbyteArray message2In,
    jbyteArray signature2In,
    jlong tagPtr) {


    const auto& [nmessage1, message1] = fromByteArray<const uint8_t*>(env, message1In);
    const auto& [nsignature1, signature1] = fromByteArray<const uint8_t*>(env, signature1In);
    const auto& [nmessage2, message2] = fromByteArray<const uint8_t*>(env, message2In);
    const auto& [nsignature2, signature2] = fromByteArray<const uint8_t*>(env, signature2In);
    const auto& tag = to_opaque_ptr(tagPtr);

    uint8_t buffer[PUBKEY_SIZE];
    TraceResult result = do_trace(
        message1, nmessage1,
        signature1, nsignature1,
        message2, nmessage2,
        signature2, nsignature2,
        &tag, &buffer);

     jobjectArray retval = (jobjectArray)env->NewObjectArray(2, env->FindClass("java/lang/Object"), NULL);
     env->SetObjectArrayElement(retval, 0, newInteger(env, -1));

     switch (result) {
        case Indep:
            env->SetObjectArrayElement(retval, 0, newInteger(env, 1));
            break;
        case Linked:
            env->SetObjectArrayElement(retval, 0, newInteger(env, 2));
            break;
        case Revealed:
            env->SetObjectArrayElement(retval, 0, newInteger(env, 3));
            break;
        case InputErrorSig1:
            triggerJavaIllegalStateException(env, "Failed to decode signature1");
            break;
        case InputErrorSig2:
            triggerJavaIllegalStateException(env, "Failed to decode signature2");
            break;
        case InputErrorTag:
            triggerJavaIllegalStateException(env, "Invalid tag pointer");
            break;
        default:
            triggerJavaIllegalStateException(env, "Unknown error");
            break;
     }
     env->SetObjectArrayElement(retval, 1, makeJByteArray(env, buffer, PUBKEY_SIZE));
     return retval;
}
