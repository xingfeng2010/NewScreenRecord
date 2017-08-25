#include <jni.h>
#include <sstream>
#include <android/log.h>
#include "audio_out.h"
#include "koala_type.h"

JavaVM* pJavaVM;
jclass ThincarPlayerPcm;
jmethodID postPcmDataFromNative;
jmethodID setPcmInfomation;
jobject thincarObj;

struct Player
{
    FILE * file;
    lec_aOut_proxy aproxy;

    int duration;
};

int write_pcm(uint8_t *pBuffer, int size, audio_info * pcm_info, void * arg)
{
    Player * player = (Player *)arg;

    __android_log_print(ANDROID_LOG_INFO, "LECPLAYERTEST","size is :%d",size);

    if (!pBuffer || !size || !pcm_info || !arg)
    {
        __android_log_print(ANDROID_LOG_INFO, "LECPLAYERTEST", "invalid pcm(ptr: 0x%x, size: %d, info: 0x%x, arg: 0x%x)",
                            pBuffer, size, pcm_info, arg);
        return 0;
    }

    int sample_bytes = 0;
    if (pcm_info->sample_fmt == KOALA_SAMPLE_FMT_U8)
    {
        sample_bytes = 1;
    }
    else if (pcm_info->sample_fmt == KOALA_SAMPLE_FMT_S16)
    {
        sample_bytes = 2;
    }
    else if (pcm_info->sample_fmt == KOALA_SAMPLE_FMT_S32)
    {
        sample_bytes = 4;
    }

    int duration = size / pcm_info->nChannles / sample_bytes * 1000 / pcm_info->sample_rate;
    player->duration += duration; ///< ms

    JNIEnv* jniEnv;
    bool isAttached = false;
    if (pJavaVM->GetEnv((void**) &jniEnv, JNI_VERSION_1_6) < 0) { //获取当前的JNIEnv
        if (pJavaVM->AttachCurrentThread(&jniEnv, NULL) < 0)
            return size;
        isAttached = true;
    }

    if (ThincarPlayerPcm == NULL) {
        ThincarPlayerPcm = jniEnv->GetObjectClass(thincarObj);
    }

    if (postPcmDataFromNative == NULL) {
        postPcmDataFromNative = jniEnv->GetMethodID(ThincarPlayerPcm,"postPcmDataFromNative","([BIII)V");
    }

    int channel = 0;
    if (pcm_info->nChannles == 1) {
        channel = 0;
    } else if (pcm_info->nChannles == 2) {
        channel = 1;
    }


//    if (setPcmInfomation == NULL) {
//        setPcmInfomation = jniEnv->GetMethodID(ThincarPlayerPcm,"setPcmInfomation","(III)V");
//    }
//    jniEnv->CallVoidMethod(thincarObj,setPcmInfomation,pcm_info->nChannles,sample_bytes,pcm_info->sample_rate);

    jbyteArray data = jniEnv->NewByteArray(size);
    jint length = size;

    jniEnv->SetByteArrayRegion(data,0,size,(jbyte *)pBuffer);
    jniEnv->CallVoidMethod(thincarObj,postPcmDataFromNative,data,channel,pcm_info->sample_fmt,pcm_info->sample_rate);

    if(data){
        jniEnv->DeleteLocalRef(data);
    }

    if (isAttached)
        pJavaVM->DetachCurrentThread();

//    if (player->file)
//    {
//        fwrite(pBuffer, 1, size, player->file);
//        fflush(player->file);
//    }
//
//    std::stringstream ss;
//    ss << "received " << size << " bytes (" << duration << "ms/" << player->duration << "ms)" << "(ch: "
//    << pcm_info->nChannles << " sr: " << pcm_info->sample_rate << " fmt: " << pcm_info->sample_fmt << ")";
//
//    __android_log_print(ANDROID_LOG_INFO, "LECPLAYERTEST", "%s", ss.str().c_str());

    return size;
}

int64_t get_pos(void * arg)
{
    //TODO: replace with your implementation
    Player * player = (Player *)arg;
    return player->duration;
}

int get_cache_duration_ms(void* arg)
{
    //TODO: replace with your implementation
    return 0;
}

void flush(void* arg)
{
    //TODO: replace with your implementation
}

extern "C"
JNIEXPORT
void JNICALL Java_com_leauto_link_lightcar_pcm_ThincarPlayerPcm_player_1ready(JNIEnv * env, jobject thiz, jobject comm, jobject arg)
{
    Player * player = new Player;

    player->duration = 0;
    player->file     = 0;

//    player->file = fopen("/storage/emulated/0/recv.pcm", "wb");
//    if (!player->file)
//    {
//        __android_log_print(ANDROID_LOG_INFO, "LECPLAYERTEST", "create file(/sdcard/recv.pcm) failed.");
//    }

    player->aproxy.write = &write_pcm;
    player->aproxy.get_pos = &get_pos;
    player->aproxy.get_cache_duration_ms = &get_cache_duration_ms;
    player->aproxy.flush = &flush;

    jclass clazz = env->FindClass("java/lang/Long");
    jfieldID id = env->GetFieldID(clazz, "value", "J");

    env->SetLongField(comm, id, (jlong)&player->aproxy);
    env->SetLongField(arg, id, (jlong)player);

    env->GetJavaVM(&pJavaVM);
    thincarObj = (jobject) env->NewGlobalRef(thiz);
}