#include <stdint.h>
#include "koala_type.h"
typedef struct lec_aOut_t lec_aOut;

enum lec_aout_flag{
    lec_aout_flag_AudioTrack,//Android
};

#define LEC_AOUT_AUDIO_TRACK_FLAG 1 << lec_aout_flag_AudioTrack
typedef struct lec_aOut_delegate_t{
    void* (*audio_out_open)(audio_info *);
    int (*write)(void* pHanlde,uint8_t *pBuffer, int size);
    int (*set_volume)(void* pHanlde,float vol);
    int64_t (*get_pos)(void* pHanlde);
    int (*get_cache_duration_ms)(void* pHandle,int64_t *in_queue,int64_t *in_cache);
    void (*flush)(void* pHanlde);
    void (*close)(void* pHanlde);
}lec_aOut_delegate;

typedef struct lec_aOut_proxy_t{
    int (*write)(uint8_t *pBuffer, int size, audio_info * pcm_info, void * arg);
    int64_t (*get_pos)(void * arg);
    int (*get_cache_duration_ms)(void* arg);
    void (*flush)(void* arg);
}lec_aOut_proxy;

lec_aOut* lec_aout_open(audio_info *pAinfo,int flags);
int lec_aout_set_proxy(lec_aOut *pHandle,lec_aOut_proxy *pAProxy,void*arg);
int lec_aout_write(lec_aOut* pHandle,uint8_t* pBuffer, int size);
int64_t lec_aout_get_pos(lec_aOut* pHandle);
int lec_aout_get_cache_duration_ms(lec_aOut* pHandle,int64_t *in_queue,int64_t *in_cache);
int lec_aout_set_volume(lec_aOut* pHandle,float vol);
void lec_aout_flush(lec_aOut* pHandle);
void lec_aout_close(lec_aOut* pHandle);
