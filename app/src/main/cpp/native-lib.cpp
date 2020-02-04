#include <string>
#include <jni.h>
#include <vector>
#include <ctime>
#include <android/log.h>



static JNIEnv* env;
static jclass java_util_ArrayList;
static jclass java_lang_Integer;
static jmethodID  java_lang_Integer_intValue;
static jmethodID java_util_ArrayList_size;
static jmethodID java_util_ArrayList_get;



void init(JNIEnv* envIn);
std::vector<int> arrayListToVector(jobject arrayList);


extern "C"
jstring
Java_com_romankaranchuk_musicplayer_utils_JniUtils_stringFromJNI(
        JNIEnv* env,
        jobject){
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
jlong
Java_com_romankaranchuk_musicplayer_utils_JniUtils_sum(
        JNIEnv* env,
        jobject /*this*/,
        jobject arrayList){
    init(env);
    std::vector<int> vector = arrayListToVector(arrayList);

    std::clock_t start_time = std::clock();
    long long sum = 0;
    for (int j = 0; j < 10000; ++j){
        for (int i = 0; i < vector.size(); ++i){
            sum += vector[i];
        }
    }
    long running_time = (long) ((std::clock() - start_time) / (double) (CLOCKS_PER_SEC / 1000));
    __android_log_print(ANDROID_LOG_DEBUG, "JNI running time", "%ld", running_time);

    return sum;
}

void init(JNIEnv* envIn){
    env = envIn;
    java_util_ArrayList         = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/util/ArrayList")));
    java_util_ArrayList_size    = env->GetMethodID(java_util_ArrayList, "size", "()I");
    java_util_ArrayList_get     = env->GetMethodID(java_util_ArrayList, "get", "(I)Ljava/lang/Object;");
    java_lang_Integer           = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/lang/Integer")));
    java_lang_Integer_intValue  = env->GetMethodID(java_lang_Integer, "intValue", "()I");
}

std::vector<int> arrayListToVector(jobject arrayList){
    unsigned long len = (unsigned long) env->CallIntMethod(arrayList, java_util_ArrayList_size);
    std::vector<int> result;
    result.reserve(len);
    for (jint i = 0; i < len; i++){
        jobject element = env->CallObjectMethod(arrayList, java_util_ArrayList_get, i);
        int value = env->CallIntMethod(element, java_lang_Integer_intValue);
        result.push_back(value);
    }
    return result;
}