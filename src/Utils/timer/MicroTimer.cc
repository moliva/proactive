#include "jni.h"
#include "MicroTimer.h"
#include <sys/time.h>
#include <stdio.h>
JNIEXPORT jlongArray JNICALL Java_org_objectweb_proactive_core_util_timer_MicroTimer_currentTime  (JNIEnv* env, jobject obj)
{
    struct timeval current; 
    struct timezone currentTZ;
    jsize size=2;
  
    jlongArray tablo = env->NewLongArray(size);
    jlong* localArray = env->GetLongArrayElements(tablo, NULL);
   
    gettimeofday(&current, &currentTZ); 
    localArray[0]=current.tv_sec;
    localArray[1]=current.tv_usec;
    // printf("Puting \n");
    env->ReleaseLongArrayElements(tablo, localArray, 0);
    return tablo;
}
